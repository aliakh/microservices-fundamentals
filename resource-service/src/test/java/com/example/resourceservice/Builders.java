package com.example.resourceservice;

import com.example.resourceservice.entity.Resource;

public interface Builders {

    static Resource buildResource() {
        var resource = new Resource();
        resource.setId(1L);
        resource.setKey("74bcaf90-df4f-4e55-bb63-5d84961c2f5a");
        return resource;
    }
}
