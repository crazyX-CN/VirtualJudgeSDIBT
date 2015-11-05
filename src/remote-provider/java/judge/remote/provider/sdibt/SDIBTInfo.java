package judge.remote.provider.sdibt;

import judge.remote.RemoteOj;
import judge.remote.RemoteOjInfo;

import org.apache.http.HttpHost;

public class SDIBTInfo {

    public static final RemoteOjInfo INFO = new RemoteOjInfo( //
            RemoteOj.SDIBT, //
            "SDIBT", //
            new HttpHost("acm.sdibt.edu.cn") //
    );
    
    static {
        INFO.faviconUrl = "images/remote_oj/SDIBT_favicon.ico";
        INFO._64IntIoFormat = "%lld & %llu";
    }

}
