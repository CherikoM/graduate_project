package com.example.project.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.project.dataobject.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @date 2019/4/25 11:46
 * @atuther net source
 */
@Component
public class JwtUtil {

    //密钥
    public static final String SECRET = "sgfdsopljkhsl;o437632";

    //accessToken过期时间，单位为小时
    public static final int ACCESS_EXPIRE = 24*3;

    public static final int REFRESH_EXPIRE = 24*30;

    public static final String REFRESH_PREFIX = "validate-test-";

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        JwtUtil.redisTemplate = redisTemplate;
    }

        /**
         * 生成AccessToken
         * @param id
         * @param name
         * @return
         * @throws Exception
         */
    public static String createUserToken(Long id, String name, int time) throws Exception {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.HOUR, time);
        Date expireDate = nowTime.getTime();

        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        String token = JWT.create()
                .withHeader(map)//头
                .withClaim("id", id)
                .withClaim("name", name)
                .withSubject("测试")//
                .withIssuedAt(new Date())//签名时间
                .withExpiresAt(expireDate)//过期时间
                .sign(Algorithm.HMAC256(SECRET));//签名
        return token;
    }


    /**
     * 获取accessToken并存储refreshToken
     * @param userDO
     * @return
     * @throws Exception
     */
    public static Map<String, String> makeUserTokens(UserDO userDO) throws Exception {
        String accessToken;
        String refreshToken;

        //表示用户已登录的accessToken，携带用户id、用户名信息
        accessToken = createUserToken(userDO.getId(), userDO.getName(), ACCESS_EXPIRE);
        //维持用户登陆状态的refreshToken，使用随机数和UUID伪装成用户id和用户名
        refreshToken = createUserToken((long) Math.floor(Math.random()*10), UUID.randomUUID().toString(), REFRESH_EXPIRE);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        redisTemplate.opsForValue().set("validate-test-" + userDO.getId(), refreshToken, 30, TimeUnit.DAYS);

        return tokens;
    }

    /**
     * 验证Token
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyUserToken(String token)throws Exception{
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT jwt = null;

        try {
            //token检测，已过期的accessToken会报异常
            jwt = verifier.verify(token);
            return jwt.getClaims();

        } catch (JWTVerificationException e) {

            throw new RuntimeException(e);
        }
    }

    public static String makeRefreshToken() throws Exception {
        String refreshToken = createUserToken((long) Math.floor(Math.random()*10), UUID.randomUUID().toString(), REFRESH_EXPIRE);
        return refreshToken;
    }


    /**
     * 解析Token
     * @param token
     * @return
     */
    public static Map<String, Claim> parseToken(String token){
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaims();
    }
}