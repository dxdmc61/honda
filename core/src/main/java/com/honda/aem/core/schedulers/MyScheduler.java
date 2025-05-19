package com.honda.aem.core.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = MyScheduler.Config.class)
@Component(
    service = Runnable.class,
    immediate = true,
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
public class MyScheduler implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(MyScheduler.class);
    
    private String cronExpression;
    private String[] myCustomParameter;
    private boolean enabled;  // New field for checkbox state
    
    @Reference
    private Scheduler scheduler;

    @ObjectClassDefinition(name = "My Scheduler", description = "Sample Demo Scheduler configuration")
    public @interface Config {
        @AttributeDefinition(
            name = "Enable Scheduler",
            description = "Checkbox to enable/disable scheduler execution",
            type = AttributeType.BOOLEAN
        )
        boolean enabled() default false;  // Checkbox defaulting to false
        
        @AttributeDefinition(name = "Cron Expression", description = "Cron Expression for scheduler")
        String cronExpression() default "*/10 * * * * ?";

        @AttributeDefinition(name = "Custom Parameter", description = "Custom Parameter")
        String[] myCustomParameter() default {};
    }

    @Override
    public void run() {
        if (!enabled) {
            LOG.debug("Scheduler is disabled - skipping execution");
            return;
        }
        
        LOG.info("Scheduler executing with parameters:");
        if (myCustomParameter != null) {
            for (String param : myCustomParameter) {
                LOG.info("- {}", param);
            }
        }
        // Your actual task logic here
    }

    @Activate
    @Modified
    protected void activate(Config config, ComponentContext context) {
        this.enabled = config.enabled();
        this.cronExpression = config.cronExpression();
        this.myCustomParameter = config.myCustomParameter();
        
        // Always unschedule first
        scheduler.unschedule((String) context.getProperties().get("component.name"));
        
        if (enabled) {
            // Only schedule if enabled
            ScheduleOptions options = scheduler.EXPR(cronExpression);
            options.canRunConcurrently(false);
            options.name(getClass().getName());
            scheduler.schedule(this, options);
            LOG.info("Scheduler ENABLED with cron: {}", cronExpression);
        } else {
            LOG.info("Scheduler DISABLED");
        }
    }

    @Deactivate
    protected void deactivate() {
        scheduler.unschedule(getClass().getName());
        LOG.info("Scheduler deactivated");
    }
}