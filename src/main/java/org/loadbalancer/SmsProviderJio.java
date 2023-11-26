package org.loadbalancer;

public class SmsProviderJio implements SmsProvider{
  public SmsProviderJio(int maxThroughput,boolean status){
    this.maxThroughput=maxThroughput;
    this.status=status;
  }
  int pendingMessageCount=0;

  public int getPendingMessageCount() {
    return pendingMessageCount;
  }

  public void setPendingMessageCount(int pendingMessageCount) {
    this.pendingMessageCount = pendingMessageCount;
  }
  public int maxThroughput;
  public boolean status=true;

  public int getMaxThroughput() {
    return maxThroughput;
  }

  public void setMaxThroughput(int maxThroughput) {
    this.maxThroughput = maxThroughput;
  }

  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public void sendSms(Details details){
      System.out.println("sending message no. "+pendingMessageCount+ " via jio");
  }
}
