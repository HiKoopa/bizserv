package com.uangel.svc.biz.modules;

import com.uangel.svc.biz.impl.ctinetty.CtiRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
@Import(CtiRouter.class)
public class NettyCtiClientModule {
}
