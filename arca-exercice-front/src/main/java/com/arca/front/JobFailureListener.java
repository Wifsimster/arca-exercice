package com.arca.front;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Job execution listener that add the failure exceptions details
 * in the job execution exit description.
 *
 * @author Thomas Vanstals
 * @see JobExecution#getAllFailureExceptions()
 * @see ExitStatus#getExitDescription()
 * @since 1.2.0
 */
@Component("jobFailureListener")
public class JobFailureListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // nothing to do
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (!jobExecution.getAllFailureExceptions().isEmpty()) {
            ExitStatus exitStatus = ExitStatus.FAILED;
            for (Throwable e : jobExecution.getAllFailureExceptions()) {
                exitStatus = exitStatus.addExitDescription(e);
            }
            jobExecution.setExitStatus(exitStatus);
        }
    }

}
