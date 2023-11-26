package org.loadbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SmsLoadBalancer {
  private List<SmsProvider> providers;
  private ExecutorService executorService;

  public SmsLoadBalancer(List<SmsProvider> providers) {
    this.providers = providers;

//    to run each available provider in parallel
    this.executorService = Executors.newFixedThreadPool(providers.size());
  }

  public Map<SmsProvider,Integer> sendSms(List<Details> phoneNumbersListWithText) {
    Map<SmsProvider,Integer>msgDistributionMap=new HashMap<>();
    List<Future<Void>> futures = new ArrayList<>();
    int size= phoneNumbersListWithText.size();
    int index=0;
    int remainingMessage=size;
    int totalThroughput=getTotalThroughput();
    for (int i = 0; i < providers.size(); i++) {
      SmsProvider provider = providers.get(i);
      int throughput = provider.getMaxThroughput();
      if(provider.getStatus()==false)continue;
      int messagesToSend;
//      dividing message on basis of throughput
      if(i==providers.size()-1){
        messagesToSend= remainingMessage;
      }
      else {
        messagesToSend = size * throughput /totalThroughput ;
        remainingMessage-=messagesToSend;
//        System.out.println("remaining "+remainingMessage);
      }
      provider.setPendingMessageCount(messagesToSend);
      msgDistributionMap.put(provider,messagesToSend);
      List<Details> subsetPhoneNumbers = phoneNumbersListWithText.subList(index,index+ messagesToSend);
      index+=messagesToSend;
//      List<Details> subsetPhoneNumbers = phoneNumbersListWithText.subList(i * messagesToSend, (i + 1) * messagesToSend %(size+1));

      Callable<Void> task = () -> {
        for (Details phoneNumberWithText : subsetPhoneNumbers) {
          provider.sendSms(phoneNumberWithText);
          provider.setPendingMessageCount(provider.getPendingMessageCount()-1);
        }
        return null;
      };

      futures.add(executorService.submit(task));
    }

    // Wait for all threads to complete
    for (Future<Void> future : futures) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    return msgDistributionMap;
  }

  private int getTotalThroughput() {
    return providers.stream()
        .filter(SmsProvider::getStatus) // only status===true will be added
        .mapToInt(SmsProvider::getMaxThroughput)
        .sum();
  }
}
