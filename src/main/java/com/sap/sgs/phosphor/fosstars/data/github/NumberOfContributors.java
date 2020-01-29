package com.sap.sgs.phosphor.fosstars.data.github;

import static com.sap.sgs.phosphor.fosstars.model.feature.oss.OssFeatures.NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS;

import com.sap.sgs.phosphor.fosstars.data.UserCallback;
import com.sap.sgs.phosphor.fosstars.model.Value;
import com.sap.sgs.phosphor.fosstars.model.value.IntegerValue;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

/**
 * This data provider counts how many people contributes to a project last 3 months. A contributor
 * is an author of a commit or a person who committed the commit. The provider iterates over all
 * commits which have been made for lat 3 months, and collect unique login names of contributors.
 */
public class NumberOfContributors extends AbstractGitHubDataProvider {

  /**
   * 90 days in millis.
   */
  private static final long DELTA = 90 * 24 * 60 * 60 * 1000L;

  /**
   * @param where A GitHub organization of user name.
   * @param name A name of a repository.
   * @param github An interface to the GitHub API.
   * @param mayTalk A flag which shows if the provider can communicate with a user or not.
   */
  public NumberOfContributors(String where, String name, GitHub github, boolean mayTalk) {
    super(where, name, github, mayTalk);
  }

  @Override
  public Value<Integer> get(UserCallback callback) throws IOException {
    System.out.println("[+] Counting how many people contributed to the project last three months ...");

    Optional<Value> something = cache().get(url, NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS);
    if (something.isPresent()) {
      return something.get();
    }

    GHRepository repository = github.getRepository(path);
    Date date = new Date(System.currentTimeMillis() - DELTA);
    Set<String> contributors = new HashSet<>();
    for (GHCommit commit : repository.listCommits()) {
      if (commit.getCommitDate().after(date)) {
        GHUser author = commit.getAuthor();
        if (author != null) {
          contributors.add(author.getLogin());
        }
        GHUser committer = commit.getCommitter();
        if (committer != null) {
          contributors.add(committer.getLogin());
        }
      } else {
        break;
      }
    }

    Value<Integer> numberOfContributors = new IntegerValue(
        NUMBER_OF_CONTRIBUTORS_LAST_THREE_MONTHS, contributors.size());
    cache().put(url, numberOfContributors, tomorrow());

    return numberOfContributors;
  }
}
