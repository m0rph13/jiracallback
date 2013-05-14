package se.istone.jiracallback;



import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueLink;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.json.gen.IssueUpdateJsonGenerator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HelloWorld {
	public static void main(String[] args) throws Exception {

		DateTime dateTime = new DateTime(new Date());
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String formatedDate = formatter.print(dateTime);



		JerseyJiraRestClientFactory f = new JerseyJiraRestClientFactory();
		JiraRestClient jc = f.createWithBasicHttpAuthentication(new URI("http://access.istone.se"), "", "");

		SearchResult r = jc.getSearchClient().searchJql("fixVersion=\"Sprint 24_3\"", null);

		IssueRestClient issueClient=jc.getIssueClient();

		Iterator<? extends BasicIssue> issues=r.getIssues().iterator();

		while (issues.hasNext()) {

			Issue issue = jc.getIssueClient().getIssue((issues.next()).getKey(), null);

			System.out.println("Issue: " + issue.getKey() + " " + issue.getSummary());
			List<FieldInput> fields = new ArrayList<FieldInput>();
			fields.add(new FieldInput("customfield_10793",formatedDate));
			issueClient.update(issue,fields,null);

		}

	}
}
