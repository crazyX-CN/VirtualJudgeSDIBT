package judge.remote.provider.acdream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import judge.httpclient.DedicatedHttpClient;
import judge.httpclient.SimpleNameValueEntityFactory;
import judge.remote.RemoteOjInfo;
import judge.remote.account.RemoteAccount;
import judge.remote.querier.AuthenticatedQuerier;
import judge.remote.status.RemoteStatusType;
import judge.remote.status.SubmissionRemoteStatus;
import judge.remote.status.SubstringNormalizer;
import judge.remote.submitter.SubmissionInfo;
import org.apache.struts2.json.JSONException;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.Validate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ACdreamQuerier extends AuthenticatedQuerier {


    private final static Logger log = LoggerFactory.getLogger(ACdreamQuerier.class);

    @Override
    public RemoteOjInfo getOjInfo() {
        return ACdreamInfo.INFO;
    }

    @Override
    protected SubmissionRemoteStatus query(SubmissionInfo info, RemoteAccount remoteAccount, DedicatedHttpClient client) throws JSONException {

        String html = client.get("/status?name="+info.remoteAccountId+"&pid="+info.remoteProblemId).getBody();

        log.info(html);
        log.info(info.remoteAccountId);
        log.info(info.remoteProblemId);
        log.info(info.remoteRunId);

        Pattern pattern = Pattern.compile("<tr class=.*?><td>"+info.remoteRunId+
            "</td><td><a .*?>"+info.remoteAccountId+
            "</a></td><td><a .*?>"+info.remoteProblemId+
            "</a></td><td .*?>(.*?)</td><td>(.*?) MS</td><td>(.*?) KB</td>");
        Matcher matcher = pattern.matcher(html);

        log.info(matcher.group(1));
        Validate.isTrue(matcher.find());

        SubmissionRemoteStatus status = new SubmissionRemoteStatus();
        status.rawStatus = matcher.group(1).replaceAll("<[^<>]*>", "").trim();
        status.statusType = SubstringNormalizer.DEFAULT.getStatusType(status.rawStatus);
        
        if (status.statusType == RemoteStatusType.AC) {
            status.executionTime = calcTime(matcher.group(2));
            status.executionMemory = calcMemory(matcher.group(3));
        }
        return status;
    }

    private int calcTime(String str) {
        Matcher matcher = Pattern.compile("(\\d+)").matcher(str);
        if (matcher.find()) {
            Integer a = Integer.parseInt(matcher.group(1), 10);
            return a ;
        }else return 0;
    }
    
    private int calcMemory(String str) {
        Matcher matcher = Pattern.compile("(\\d+)").matcher(str);
        if (matcher.find()) {
            Integer a = Integer.parseInt(matcher.group(1), 10);
            return a ;
        }else return 0;
    }
}
