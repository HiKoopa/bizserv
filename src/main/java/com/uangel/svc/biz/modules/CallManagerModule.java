package com.uangel.svc.biz.modules;

import com.uangel.svc.biz.impl.callactor.CallManagerImpl;
import com.uangel.svc.biz.impl.callactor.CallManagerRequires;
import com.uangel.svc.biz.impl.ctinetty.CtiRequires;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
@Import({CallManagerImpl.class, CallManagerRequires.class})
public class CallManagerModule {
}
