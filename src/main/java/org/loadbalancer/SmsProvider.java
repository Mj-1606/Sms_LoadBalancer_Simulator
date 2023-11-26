package org.loadbalancer;

public interface SmsProvider {
  static String name="";
     int maxThroughput=0;

     boolean status=true;int getMaxThroughput();

     void setMaxThroughput(int maxThroughput);

     boolean getStatus();

  int pendingMessageCount=0;

  int getPendingMessageCount() ;

  void setPendingMessageCount(int pendingMessageCount) ;

     void setStatus(boolean status);

    void sendSms(Details details);
}
