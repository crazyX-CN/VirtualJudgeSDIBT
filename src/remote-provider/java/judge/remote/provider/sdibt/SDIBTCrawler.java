package judge.remote.provider.sdibt;

import judge.remote.RemoteOjInfo;
import judge.remote.crawler.RawProblemInfo;
import judge.remote.crawler.SimpleCrawler;
import judge.tool.Tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

@Component
public class SDIBTCrawler extends SimpleCrawler {

    @Override
    public RemoteOjInfo getOjInfo() {
        return SDIBTInfo.INFO;
    }

    @Override
    protected String getProblemUrl(String problemId) {
        return getHost().toURI() + "/JudgeOnline/problem.php?id=" + problemId;
    }
    
    @Override
    protected void preValidate(String problemId) {
        Validate.isTrue(problemId.matches("[1-9]\\d*"));
    }

    @Override
    protected void populateProblemInfo(RawProblemInfo info, String problemId, String html) {

        info.title = Tools.regFind(html, "</title><center><h2>([\\s\\S]*?)</h2>").trim();
        
        info.timeLimit = (1000*Integer.parseInt(Tools.regFind(html, "(\\d*) Sec")));
        
        info.memoryLimit = (1024*Integer.parseInt(Tools.regFind(html, "(\\d*) MB")));
        
        info.description = (Tools.regFind(html, "<h2>Description</h2>([\\s\\S]*?)<h2>Input</h2>"));
        
        info.input = (Tools.regFind(html, "<h2>Input</h2>([\\s\\S]*?)<h2>Output</h2>"));
        
        info.output = (Tools.regFind(html, "<h2>Output</h2>([\\s\\S]*?)<h2>Sample Input</h2>"));
        
        info.sampleInput = (Tools.regFind(html, "<h2>Sample Input</h2>([\\s\\S]*?)<h2>Sample Output</h2>"));
        
        info.sampleOutput = (Tools.regFind(html, "<h2>Sample Output</h2>([\\s\\S]*?)<h2>HINT</h2>"));
        
        info.hint = (Tools.regFind(html, "<h2>HINT</h2>([\\s\\S]*?)<h2>Source</h2>"));
        
        info.source = (Tools.regFind(html, "<h2>Source</h2>([\\s\\S]*?)<center>").replaceAll("<[\\s\\S]*?>", ""));
    }

}
