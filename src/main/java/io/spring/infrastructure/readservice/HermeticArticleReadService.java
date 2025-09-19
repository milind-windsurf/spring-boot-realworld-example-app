package io.spring.infrastructure.readservice;

import io.spring.application.CursorPageParameter;
import io.spring.application.Page;
import io.spring.application.data.ArticleData;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.service.SeedingService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hermetic")
public class HermeticArticleReadService implements ArticleReadService {
  private final SeedingService seedingService;

  @Autowired
  public HermeticArticleReadService(SeedingService seedingService) {
    this.seedingService = seedingService;
  }

  @Override
  public ArticleData findById(String id) {
    return (ArticleData) seedingService.getSeed("articledata:" + id);
  }

  @Override
  public ArticleData findBySlug(String slug) {
    return (ArticleData) seedingService.getSeed("article:" + slug);
  }

  @Override
  public List<String> queryArticles(String tag, String author, String favoritedBy, Page page) {
    List<String> allArticleIds = getAllArticleIds();
    return allArticleIds.stream()
        .skip(page.getOffset())
        .limit(page.getLimit())
        .collect(Collectors.toList());
  }

  @Override
  public int countArticle(String tag, String author, String favoritedBy) {
    return getAllArticleIds().size();
  }

  @Override
  public List<ArticleData> findArticles(List<String> articleIds) {
    return articleIds.stream()
        .map(id -> (ArticleData) seedingService.getSeed("articledata:" + id))
        .filter(article -> article != null)
        .collect(Collectors.toList());
  }

  @Override
  public List<ArticleData> findArticlesOfAuthors(List<String> authors, Page page) {
    List<ArticleData> allArticles = getAllArticles();
    return allArticles.stream()
        .filter(article -> authors.contains(article.getProfileData().getId()))
        .skip(page.getOffset())
        .limit(page.getLimit())
        .collect(Collectors.toList());
  }

  @Override
  public List<ArticleData> findArticlesOfAuthorsWithCursor(List<String> authors, CursorPageParameter page) {
    List<ArticleData> allArticles = getAllArticles();
    return allArticles.stream()
        .filter(article -> authors.contains(article.getProfileData().getId()))
        .limit(page.getLimit())
        .collect(Collectors.toList());
  }

  @Override
  public int countFeedSize(List<String> authors) {
    List<ArticleData> allArticles = getAllArticles();
    return (int) allArticles.stream()
        .filter(article -> authors.contains(article.getProfileData().getId()))
        .count();
  }

  @Override
  public List<String> findArticlesWithCursor(String tag, String author, String favoritedBy, CursorPageParameter page) {
    List<String> allArticleIds = getAllArticleIds();
    return allArticleIds.stream()
        .limit(page.getLimit())
        .collect(Collectors.toList());
  }

  private List<String> getAllArticleIds() {
    List<ArticleData> articles = getAllArticles();
    return articles.stream()
        .map(ArticleData::getId)
        .collect(Collectors.toList());
  }

  private List<ArticleData> getAllArticles() {
    List<ArticleData> articles = new ArrayList<>();
    Object dragonArticle = seedingService.getSeed("article:how-to-train-your-dragon");
    Object catArticle = seedingService.getSeed("article:how-to-train-your-cat");
    
    if (dragonArticle instanceof ArticleData) {
      articles.add((ArticleData) dragonArticle);
    }
    if (catArticle instanceof ArticleData) {
      articles.add((ArticleData) catArticle);
    }
    
    return articles;
  }
}
