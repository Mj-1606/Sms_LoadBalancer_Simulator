

import org.loadbalancer.SmsProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.loadbalancer.Details;
import org.loadbalancer.SmsLoadBalancer;
import org.loadbalancer.SmsProviderAirtel;
import org.loadbalancer.SmsProviderJio;
import org.loadbalancer.SmsProviderVI;

public class SimulatorTest {
@Test
void test1(){
  // case when each SmsProvider have equal throughput
  List<SmsProvider> SmsProviders = new ArrayList<>();
  SmsProviders.add(new SmsProviderAirtel(10,true));
  SmsProviders.add(new SmsProviderJio(10,true));
  SmsProviders.add(new SmsProviderVI(10,true));

  // Creating SmsLoadBalancer instance
  int totalMessages=30;

//  making list of phones and texts
  List<Details> detailsList = new ArrayList<>();
  for (int i = 0; i < totalMessages; i++) {
    detailsList.add(new Details (String.valueOf(123456780+i), "Test SMS "));
  }
  // Simulate sending SMS
  SmsLoadBalancer smsLoadBalancer=new SmsLoadBalancer(SmsProviders);
  Map<SmsProvider,Integer>msgDistributionMap= smsLoadBalancer.sendSms(detailsList);
  Iterator<Entry<SmsProvider, Integer>> iterator = msgDistributionMap.entrySet().iterator();
  while (iterator.hasNext()) {
    Map.Entry<SmsProvider, Integer> entry = iterator.next();
    SmsProvider key = entry.getKey();
    Integer value = entry.getValue();
    Assertions.assertEquals(10,value);
  }

}
@Test
  void test2(){
    // case when each SmsProvider have unequal throughput
    List<SmsProvider> SmsProviders = new ArrayList<>();
    SmsProvider airtel=new SmsProviderAirtel(20,true);
    SmsProvider jio=new SmsProviderJio(10,true);
    SmsProvider vi=new SmsProviderVI(10,true);
    SmsProviders.add(airtel);
    SmsProviders.add(jio);
    SmsProviders.add(vi);

    // Creating SmsLoadBalancer instance
    int totalMessages=40;
//  making list of phones and texts
    List<Details> detailsList = new ArrayList<>();
    for (int i = 0; i < totalMessages; i++) {
      detailsList.add(new Details (String.valueOf(123456780+i), "Test SMS "));
    }
    // Simulate sending SMS
    SmsLoadBalancer smsLoadBalancer=new SmsLoadBalancer(SmsProviders);
  Map<SmsProvider,Integer>msgDistributionMap= smsLoadBalancer.sendSms(detailsList);
  Assertions.assertEquals(20,msgDistributionMap.get(airtel));
  Assertions.assertEquals(10,msgDistributionMap.get(jio));
  Assertions.assertEquals(10,msgDistributionMap.get(vi));

  }

  @Test
  void test3(){
    // case when one of SmsProvider is down
    List<SmsProvider> SmsProviders = new ArrayList<>();
    SmsProvider airtel=new SmsProviderAirtel(10,true);
    SmsProvider jio=new SmsProviderJio(10,false);
    SmsProvider vi=new SmsProviderVI(20,true);
    SmsProviders.add(airtel);
    SmsProviders.add(jio);
    SmsProviders.add(vi);

    int totalMessages=60;

//  making list of phones and texts
    List<Details> detailsList = new ArrayList<>();
    for (int i = 0; i < totalMessages; i++) {
      detailsList.add(new Details (String.valueOf(123456780+i), "Test SMS "));
    }
    // Creating SmsLoadBalancer instance
    SmsLoadBalancer smsLoadBalancer=new SmsLoadBalancer(SmsProviders);
    // Simulate sending SMS
    Map<SmsProvider,Integer>msgDistributionMap= smsLoadBalancer.sendSms(detailsList);
    Assertions.assertEquals(20,msgDistributionMap.get(airtel));
    Assertions.assertEquals(0,msgDistributionMap.getOrDefault(jio,0));
    Assertions.assertEquals(40,msgDistributionMap.get(vi));
  }
}
