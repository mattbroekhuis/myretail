//
// Built on Mon Oct 05 01:16:44 CEST 2015 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.TRACE


def String getProp(String key){
    return System.getProperty(key) ? System.getProperty(key) : System.getenv(key)
}

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n%ex{full, DISPLAY_EX_EVAL}"
    }
}
/** dumps out the mapped endpoints on startup */
logger("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping", INFO)
logger("com.myretail", INFO)
//cql migration output
logger("com.contrastsecurity.cassandra", INFO)


String logmode = getProp("loggingMode");

if("developer".equalsIgnoreCase(logmode)){
    logger("com.myretail", DEBUG)
    logger("org.hibernate.SQL", DEBUG)
    logger("org.hibernate.type", TRACE)
    logger("org.hibernate.tool.hbm2ddl", DEBUG)
    logger("org.springframework.orm.jpa", DEBUG)
    logger("logger.org.springframework.transaction", DEBUG)
}

root(ERROR, ["STDOUT"])


