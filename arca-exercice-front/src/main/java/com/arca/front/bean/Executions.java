package com.arca.front.bean;

public class Executions {
    private String status;
    private String exitStatus;
    private String readCount;
    private String writeCount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(String exitStatus) {
        this.exitStatus = exitStatus;
    }

    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    public String getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(String writeCount) {
        this.writeCount = writeCount;
    }
}
