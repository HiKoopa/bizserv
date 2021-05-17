package com.uangel.svc.biz.cti;

import com.uangel.svc.biz.modules.NettyCtiClientModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {NettyCtiClientModule.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)

public class TestCtiClient {

    @Autowired
    CtiClient ctiClient;

    @Test
    public void Test() {
    }
}
