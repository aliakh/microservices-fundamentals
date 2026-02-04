package com.example.resourceservice;

import com.example.resourceservice.entity.Resource;

public interface Builders {

    static Resource buildResource() {
        var resource = new Resource();
        resource.setId(1L);
        resource.setKey("45453da8-e24f-4eea-86bf-8ca651a54bc6");
        return resource;
    }
}
