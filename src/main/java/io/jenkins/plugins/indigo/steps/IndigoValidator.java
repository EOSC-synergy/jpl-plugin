package io.jenkins.plugins.indigo.steps;

import java.io.Serializable;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.MissingContextVariableException;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import eu.indigo.compose.parser.ConfigValidation;

public class IndigoValidator extends Step {
    protected String yml;
    protected String schema;

    public String getYml() {
        return this.yml;
    }

    @DataBoundSetter
    public void setYml(String yml) {
        this.yml = yml;
    }

    public String getSchema() {
        return this.schema;
    }

    @DataBoundSetter
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @DataBoundConstructor
    public IndigoValidator(@Nonnull String yml, @Nonnull String schema) {
        if (isBlank(yml)) {
            throw new IllegalArgumentException("yml parameter must be provided to indigoValidator");
        }
        this.yml = yml;
        if (isBlank(schema)) {
            throw new IllegalArgumentException("schema parameter must be provided to indigoValidator");
        }
        this.schema = schema;
    }

    @Extension
	public static class DescriptorImpl extends StepDescriptor {

		public DescriptorImpl() {
		}

		@Override
		public String getFunctionName() {
			return "indigoValidator";
		}

		@Override
		@Nonnull
		public String getDisplayName() {
			return "Check config.yml over shared library provided schema.";
        }
        
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }

	}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(context, this);
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Set> {
        private static final long serialVersionUID = 0L;
        private transient IndigoValidator step;

        protected String yml;
        protected String schema;

        protected Execution(@Nonnull StepContext context, IndigoValidator step) {
            super(context);
            this.step = step;
        }

        @Override
        protected final Set run() throws Exception {
            return doRun();
        }

        protected Set doRun() throws Exception {
            String yml = step.getYml();
            String schema = step.getSchema();

            if (isNotBlank(yml) && isNotBlank(schema)) {
                ConfigValidation configValidation = new ConfigValidation();
                return configValidation.validate(yml, schema);
            } else {
                throw new MissingContextVariableException(IndigoValidator.class);
            }
        }

    }
    
}
