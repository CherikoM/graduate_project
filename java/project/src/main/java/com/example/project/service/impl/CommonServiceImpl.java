package com.example.project.service.impl;

import com.example.project.common.Paging;
import com.example.project.common.Result;
import com.example.project.dao.ArtistDAO;
import com.example.project.dao.AuditorDAO;
import com.example.project.dao.LabelDAO;
import com.example.project.dao.ReleaseDAO;
import com.example.project.dataobject.ArtistDO;
import com.example.project.dataobject.AuditorDO;
import com.example.project.dataobject.LabelDO;
import com.example.project.dataobject.ReleaseDO;
import com.example.project.model.Label;
import com.example.project.model.Release;
import com.example.project.service.CommonService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private ReleaseDAO releaseDAO;

    @Autowired
    private ArtistDAO artistDAO;

    @Autowired
    private LabelDAO labelDAO;

    @Override
    public Paging info(String keyword, List<String> genres, List<String> styles, List<String> formats, List<String> forms, List<String> countries, List<String> decades, int currentPage, int pageSize, int order, String area) {
        Page page = null;

        List result = null;

        Map<String, String> decadeMap = null;
        if(area.equals("releases")) {
            //我找了一下午方法，但还是没找到怎么让前端不传参时这里收到null，所以先用死办法了
            //如果参数量小的话用死办法无伤大雅，但这里参数量多，非常尴尬
            if(decades != null) {
                decadeMap = new HashMap<>();
                for(String decade: decades) {
                    String begin;
                    if(decade.equals("0")) {
                        begin = "0000-01-01";
                    } else {
                        begin = decade + "-01-01";
                    }
                    String end;
                    if(decade.equals("0")) {
                        end = "1879-12-31";
                    } else {
                        end = decade.substring(0, 3) + "9-12-31";
                    }
                    decadeMap.put(begin, end);
                }
            }

            Map<String, String> finalDecadeMap = decadeMap;
            page = PageHelper.startPage(currentPage, pageSize).doSelectPage(
                    ()-> releaseDAO.search(keyword, genres, styles, formats, forms, countries, finalDecadeMap, order));

            List<ReleaseDO> releaseDOs = page.getResult();
            result = releaseDOs.stream().map(ReleaseDO::toModel).collect(Collectors.toList());
        } else if(area.equals("artists")) {
            page = PageHelper.startPage(currentPage, pageSize).doSelectPage(
                    ()-> artistDAO.search(keyword, order)
            );

            List<ArtistDO> artistDOs = page.getResult();
            result = artistDOs.stream().map(ArtistDO::toModel).collect(Collectors.toList());
        } else if (area.equals("labels")) {
            page = PageHelper.startPage(currentPage, pageSize).doSelectPage(
                    ()-> labelDAO.search(keyword, order)
            );

            List<LabelDO> labelDOs = page.getResult();
            result = labelDOs.stream().map(LabelDO::toModel).collect(Collectors.toList());
        }

        if(page != null) {
            return new Paging<>(page.getPageNum(),
                    page.getPageSize(),
                    page.getPages(),
                    page.getTotal(),
                    result);
        } else {
            return null;
        }
    }
}
