package com.example.project.api;

import com.auth0.jwt.interfaces.Claim;
import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dataobject.UserDO;
import com.example.project.model.User;
import com.example.project.service.UserService;
import com.example.project.util.JwtUtil;
import com.example.project.util.KeywordUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 与user相关的操作的api
 */
@Controller
@RequestMapping("/user")
public class UserAPI {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "* */15 * * * ?")
    public void addContributePoint() {
        userService.addContributePoint();
    }

    @Scheduled(cron = "* */15 * * * ?")
    public void addFollowFan() {
        userService.addFollowFan();
        userService.addAuditPoint();
    }

    /**
     * 每天删除一次今日审核数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void delTodayAudit() {
        userService.clearTodayAudit();
    }

    /**
     * user注册
     * @param userDO
     * @param session
     * @return
     */
    @PutMapping("/register")
    @ResponseBody
    public Result register(@RequestBody UserDO userDO, HttpSession session) {

        return userService.register(userDO, session);
    }

    /**
     * user登录
     * @param userDO
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestBody UserDO userDO, HttpSession session) {
        return userService.login(userDO, session);
    }

    @PostMapping("/logout/{userId}")
    @ResponseBody
    public Result logout(@PathVariable("userId")Long userId) {
        return userService.logout(userId);
    }

    @GetMapping("/id")
    @ResponseBody
    public Result getUserById(@RequestParam("targetId")Long targetId, @RequestParam("userId")Long userId) {
        User user = userService.getUserById(targetId, userId);
        if(user==null) {
            return Result.error("Get user by id failed!");
        } else {
            return Result.success(user);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Result updateUser(@RequestBody UserDO userDO) {
        int num = userService.updateUser(userDO);
        if(num==1) {
            return Result.success("Update user info success!");
        } else {
            return Result.success("Update user info failed!");
        }
    }

    /**
     * 检查或更新user token
     * @param request
     * @return
     */
    @GetMapping("/checkToken")
    @ResponseBody
    public Result refreshToken(HttpServletRequest request) {
        String accessToken = request.getHeader("accessToken");
        String refreshToken = request.getHeader("refreshToken");
        String shouldRefresh = request.getHeader("shouldRefresh");

        Map<String, Claim> infoMap;

        //解析token，获取用户信息
        infoMap = JwtUtil.parseToken(accessToken);
        Long id = infoMap.get("id").asLong();
        String name = infoMap.get("name").asString();

        //token已失效
        try {
            JwtUtil.verifyUserToken(accessToken);
        } catch (Exception e) {
            redisTemplate.delete(JwtUtil.REFRESH_PREFIX+id);
            return Result.error("登陆状态已失效，请重新登录！");
        }

        //前端判断需要刷新token（前端持有的accessToken即将过期）
        if(shouldRefresh.equals("true")) {

            //在redis中取得refreshToken
            String checkRefreshToken = (String) redisTemplate.opsForValue().get(JwtUtil.REFRESH_PREFIX+id);

            //判断前后端的refreshToken是否可以对应
            if(refreshToken.equals(checkRefreshToken)) {

                Map resultMap = null;

                try {
                    //可以对应，获取新的accessToken
                    String newAccessToken = JwtUtil.createUserToken(id, name, JwtUtil.ACCESS_EXPIRE);

                    resultMap = new HashMap<>();
                    resultMap.put("accessToken", newAccessToken);

                    Long restTime = redisTemplate.getExpire(JwtUtil.REFRESH_PREFIX+id, TimeUnit.MINUTES);

                    //如果refreshToken也即将过期
                    if(restTime < 30) {
                        //生成新的refreshToken并存入redis
                        String newRefreshToken = JwtUtil.makeRefreshToken();
                        redisTemplate.opsForValue().set(JwtUtil.REFRESH_PREFIX+id, newRefreshToken);

                        resultMap.put("refreshToken", newRefreshToken);
                    }

                    //返回token
                    return Result.success(resultMap);

                } catch (Exception e) {
                    //出任何错误都标识为登陆状态失效
                    redisTemplate.delete(JwtUtil.REFRESH_PREFIX+id);
                    return Result.error("登陆状态已失效，请重新登录！");
                }

            } else {
                //前后端的refreshToken对应不上
                redisTemplate.delete(JwtUtil.REFRESH_PREFIX+id);
                return Result.error("登陆状态已失效，请重新登录！");
            }
        } else {
            return Result.success(null);
        }

    }

    /**
     * 检查audit资格
     * @param auditorId
     * @param userId
     * @return
     */
    @GetMapping("/audit/verify")
    @ResponseBody
    public Result auditVerify(@RequestParam("auditorId") Long auditorId,@RequestParam("userId") Long userId) {
        return userService.auditVerify(auditorId, userId);
    }

    @GetMapping("/audit/data")
    @ResponseBody
    public Result getAuditData(@RequestParam("auditorId") Long auditorId) {
        return userService.getAuditData(auditorId);
    }

    /**
     * 通过userId获取全部贡献
     * @param userId
     * @return
     */
    @GetMapping("/contribute/all")
    @ResponseBody
    public Result getUserContribute(@RequestParam(value="contributorId") Long contributorId,
                                    @RequestParam(value="userId") Long userId,
                                    @RequestParam(value="pageNum", defaultValue="1") int pageNum,
                                    @RequestParam(value="pageSize", defaultValue="15") int pageSize) {
        Paging result = userService.getUserContribute(contributorId, userId, pageNum, pageSize);
        if(result.getData()==null) {
            return Result.error("Get user contribute failed!");
        } else {
            return Result.success(result);
        }
    }


    /**
     * 对用户贡献记录进行点赞
     * @param area
     * @param historyId
     * @param userId
     * @return
     */
    @GetMapping("/contribute/doThank")
    @ResponseBody
    public Result doThankContribute(@RequestParam("area")String area, @RequestParam("historyId")Long historyId, @RequestParam("userId")Long userId) {
        long result = userService.doThankContribute(area, historyId, userId);
        if(result==1) {
            return Result.success("thank contribute success!");
        } else {
            return Result.error("repetitive thank!");
        }
    }

    /**
     * 对用户贡献记录进行取消感谢
     * @param area
     * @param historyId
     * @param userId
     * @return
     */
    @GetMapping("/contribute/undoThank")
    @ResponseBody
    public Result undoThankContribute(@RequestParam("area")String area, @RequestParam("historyId")Long historyId, @RequestParam("userId")Long userId) {
        long result = userService.undoThankContribute(area, historyId, userId);
        if(result==1) {
            return Result.success("cancel thank contribute success!");
        } else {
            return Result.error("repetitive cancel thank!");
        }
    }

    @GetMapping("/getPost")
    @ResponseBody
    public Result<Map> getPost(@RequestParam("area")String area, @RequestParam("historyId")Long historyId, @RequestParam("userId")Long userId) {
        return Result.success(userService.getPost(area, historyId, userId));
    }

    @GetMapping("/follow/do/{target}")
    @ResponseBody
    public Result follow(@PathVariable("target")Long target, @RequestParam("userId") Long userId, @RequestParam("userName") String userName) {
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        if(userService.isFollow(target, userId)) {
            return Result.error("You're already follow "+target+"!");
        } else {
            userService.follow(target, user);
            return Result.success("Follow "+target+" success!");
        }
    }

    @GetMapping("/follow/undo/{target}")
    @ResponseBody
    public Result unFollow(@PathVariable("target")Long target, @RequestParam("userId") Long userId, @RequestParam("userName") String userName) {
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        if(userService.isFollow(target, userId)) {
            userService.unFollow(target, user);
            return Result.success("Follow "+target+" cancel success!");
        } else {
            return Result.error("You're already unFollow "+target+"!");
        }
    }

    @GetMapping("/follow/myFollows")
    @ResponseBody
    public Result myFollows(@RequestParam("userId")Long userId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return Result.success(userService.myFollows(userId, pageNum, pageSize));
    }

    @GetMapping("/follow/myFollowers")
    @ResponseBody
    public Result myFollowers(@RequestParam("userId")Long userId, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return Result.success(userService.myFollowers(userId, pageNum, pageSize));
    }

    @GetMapping("/test")
    @ResponseBody
    public Result test() {
        userService.addAuditPoint();
        return Result.success("aaaaa");
    }
}
