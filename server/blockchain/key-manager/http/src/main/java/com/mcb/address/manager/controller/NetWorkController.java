package com.mcb.address.manager.controller;

import com.mcb.address.manager.model.GenericResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:网络测试，临时class
 * @auther: conanliu
 * @date: 18-11-20 19:19
 */
@Slf4j
@RestController
@RequestMapping(value = "/checkNetWork")
public class NetWorkController {

    static ThreadLocal<Long> startTime = new ThreadLocal();

    @GetMapping(value = "/{uuid}")
    public GenericResponse<NetWork> checkNetWork(@PathVariable("uuid") String uuid) throws Exception  {
        startTime.set(System.currentTimeMillis());
        return ControllerTemplate.call(response -> {
            NetWork netWork = new NetWork();
            netWork.setUuid(uuid);
            netWork.setElapseMills(System.currentTimeMillis() - startTime.get());
            response.setData(netWork);
            response.setSuccess(true);
            log.info("request uuid={} consume {} ms", uuid, (System.currentTimeMillis() - startTime.get()));
        });
    }

    //网络测试，临时class
    @Getter
    @Setter
    @JsonIgnoreProperties(value = {"imadmin"}, allowSetters = true)
    @ToString
    public class NetWork {
        private String uuid;
        private Long elapseMills;
    }
}
