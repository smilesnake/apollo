package com.ctrip.framework.apollo.portal.spi.ctrip;

import com.ctrip.framework.apollo.common.customize.LoggingCustomizer;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 业务日志定制器
 */
@Component
@Profile("ctrip")
public class BizLoggingCustomizer extends LoggingCustomizer {

  /**
   * 界面配置
   */
  private final PortalConfig portalConfig;

  public BizLoggingCustomizer(final PortalConfig portalConfig) {
    this.portalConfig = portalConfig;
  }


  @Override
  protected String cloggingUrl() {
    return portalConfig.cloggingUrl();
  }

  @Override
  protected String cloggingPort() {
    return portalConfig.cloggingPort();
  }
}
