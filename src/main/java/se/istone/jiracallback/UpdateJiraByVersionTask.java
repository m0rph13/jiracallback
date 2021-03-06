package se.istone.jiracallback;


import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;

public class UpdateJiraByVersionTask extends Task
{
	private static final Logger LOG = Logger.getLogger(UpdateJiraByVersionTask.class);

	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


	private String fixVersion;
	private String jiraEnvironmentField;
	private String URL;
	private String username;
	private String password;

	public void setFixVersion(String fixVersion)
	{
		this.fixVersion = fixVersion;
	}

	public void setJiraEnvironmentField(String jiraEnvironmentField)
	{
		this.jiraEnvironmentField = jiraEnvironmentField;
	}

	public void setURL(String URL)
	{
		this.URL = URL;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public static void main(String[] args) throws Exception
	{
		UpdateJiraByVersionTask task = new UpdateJiraByVersionTask();
		task.setFixVersion(args[0]);
		task.setJiraEnvironmentField(args[1]);
		task.setURL(args[2]);
		task.setUsername(args[3]);
		task.setPassword(args[4]);
		task.execute();

	}

	public void execute()
	{
		try
		{
			final DateTime dateTime = new DateTime(new Date());

			final String formattedDate = formatter.print(dateTime);

			final JerseyJiraRestClientFactory f = new JerseyJiraRestClientFactory();
			final JiraRestClient jc = f.createWithBasicHttpAuthentication(new URI(URL), username, password);

			final SearchResult r = jc.getSearchClient().searchJql("fixVersion=\"" + fixVersion + "\"", null);

			final IssueRestClient issueClient = jc.getIssueClient();

			for (final BasicIssue basicIssue : r.getIssues())
			{
				final Issue issue = jc.getIssueClient().getIssue((basicIssue).getKey(), null);
				List<FieldInput> fields = new ArrayList<FieldInput>();
				fields.add(new FieldInput(jiraEnvironmentField, formattedDate));
				LOG.debug(format("Setting %s to %s for issue %s",jiraEnvironmentField,formattedDate,issue.getKey()));
				issueClient.update(issue, fields, null);
			}
		} catch (final Exception e)
		{
			throw new BuildException(e);
		}
	}
}
