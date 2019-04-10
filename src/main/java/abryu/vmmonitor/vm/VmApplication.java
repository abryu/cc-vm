package abryu.vmmonitor.vm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class VmApplication {

  static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

  public static void main(String[] args)

  {

    logger.debug("Starting VmApplication ...");

    SpringApplication.run(VmApplication.class, args);
  }


}
