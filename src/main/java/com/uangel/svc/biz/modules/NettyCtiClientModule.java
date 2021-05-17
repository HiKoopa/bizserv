package com.uangel.svc.biz.modules;

import com.uangel.svc.biz.impl.ctinetty.NettyCtiRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(NettyCtiRouter.class)
public class NettyCtiClientModule {
}
