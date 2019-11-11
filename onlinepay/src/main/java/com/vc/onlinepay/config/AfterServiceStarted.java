package com.vc.onlinepay.config;

import com.vc.onlinepay.http.HttpClientTools;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class AfterServiceStarted implements ApplicationRunner {

    public Logger logger = LoggerFactory.getLogger (getClass ());

    /**
     * 会在服务启动完成后立即执行
     */
    @Override
    public void run (ApplicationArguments args) throws Exception {
        FutureTask<String> task = new FutureTask<String> (new Callable<String> () {
            @Override
            public String call () throws Exception {
                return "Collection Completed";
            }
        });
        new Thread (task).start ();
    }

    /**
     * @描述:初始化
     * @作者:nada
     * @时间:2019/1/2
     **/
    private void init (int port) {
        //http://127.0.0.1:8181/onlinepay/test/checkMeiFuBao
        String url = "http://127.0.0.1:" + port + "/onlinepay/test/checkMeiFuBao";
        try {
            String result = HttpClientTools.sendGet (url, "");
            System.out.println (result);
        } catch (Exception e) {
            logger.error ("初始化异常{}",url,e);
        }
    }
}
