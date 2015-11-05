package judge.remote.provider.hust;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import judge.httpclient.DedicatedHttpClient;
import judge.httpclient.HttpStatusValidator;
import judge.remote.RemoteOjInfo;
import judge.remote.account.RemoteAccount;
import judge.remote.querier.AuthenticatedQuerier;
import judge.remote.status.RemoteStatusType;
import judge.remote.status.SubmissionRemoteStatus;
import judge.remote.status.SubstringNormalizer;
import judge.remote.submitter.SubmissionInfo;
import judge.tool.Tools;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

@Component
public class HUSTQuerier extends AuthenticatedQuerier {

    @Override
    public RemoteOjInfo getOjInfo() {
        return HUSTInfo.INFO;
    }

    @Override
    protected SubmissionRemoteStatus query(SubmissionInfo info, RemoteAccount remoteAccount, DedicatedHttpClient client) {
        String html = client.get("/solution/source/" + info.remoteRunId, HttpStatusValidator.SC_OK).getBody();

        String regex = 
                "<span class=\"badge\">(.*?)</span>\\s*结果[\\s\\S]*?" +
                "<span class=\"badge\">(\\d+)ms</span>\\s*耗时[\\s\\S]*?" +
                "<span class=\"badge\">(\\d+)kb</span>\\s* 内存";
        Matcher matcher = Pattern.compile(regex).matcher(html);
        Validate.isTrue(matcher.find());
        
        SubmissionRemoteStatus status = new SubmissionRemoteStatus();
        status.rawStatus = matcher.group(1).trim();
        status.executionTime = Integer.parseInt(matcher.group(2));
        status.executionMemory = Integer.parseInt(matcher.group(3));
        status.statusType = SubstringNormalizer.DEFAULT.getStatusType(status.rawStatus);
        if (status.statusType == RemoteStatusType.CE) {
            status.compilationErrorInfo = (Tools.regFind(html, "(<pre class=\"col-sm-12 linenums\">[\\s\\S]*?</pre>)"));
        }
        return status;
    }
    
}
