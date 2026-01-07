package io.github.jinganix.admin.starter.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.helper.auth.token.TokenService;
import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import io.github.jinganix.admin.starter.proto.lib.pageable.PagingPb;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.service.error.ErrorMessage;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import io.github.jinganix.webpb.runtime.common.InQuery;
import java.lang.reflect.ParameterizedType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
public class TestHelper implements ApplicationContextAware {

  private final ObjectMapper objectMapper =
      JsonMapper.builder()
          .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
          .annotationIntrospector(
              new JacksonAnnotationIntrospector() {
                @Override
                public boolean hasIgnoreMarker(MapperConfig<?> config, AnnotatedMember m) {
                  return super.hasIgnoreMarker(config, m) || m.hasAnnotation(InQuery.class);
                }
              })
          .build();

  @Autowired private MockMvc mockMvc;

  @Autowired private TokenService tokenService;

  private Repositories repositories;

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    repositories = new Repositories(context);
  }

  @SuppressWarnings("unchecked")
  private static <T> T getFirstActualTypeInstance(Object obj) {
    try {
      ParameterizedType type = ((ParameterizedType) obj.getClass().getGenericSuperclass());
      Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
      return clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private JpaRepository<AbstractEntity, ?> getRepository(Class<?> type) {
    return (JpaRepository<AbstractEntity, ?>) repositories.getRepositoryFor(type).orElse(null);
  }

  public void clearAll() {
    for (Class<?> type : repositories) {
      JpaRepository<AbstractEntity, ?> repository = getRepository(type);
      if (repository.count() > 0) {
        repository.deleteAllInBatch();
      }
    }
  }

  public void insertEntities(AbstractEntity... entities) {
    for (AbstractEntity entity : entities) {
      JpaRepository<AbstractEntity, ?> repository = getRepository(entity.getClass());
      repository.saveAndFlush(entity);
    }
  }

  public ResultActions request(WebpbMessage message) throws Exception {
    HttpMethod method = HttpMethod.valueOf(message.webpbMeta().getMethod());
    return mockMvc.perform(
        MockMvcRequestBuilders.request(method, WebpbUtils.formatUrl(message))
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebpbUtils.serialize(message)));
  }

  public ResultActions request(Long userId, WebpbMessage message) throws Exception {
    HttpMethod method = HttpMethod.valueOf(message.webpbMeta().getMethod());
    String token = tokenService.generate(userId);
    return mockMvc.perform(
        MockMvcRequestBuilders.request(method, WebpbUtils.formatUrl(message))
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebpbUtils.serialize(message)));
  }

  public <T extends WebpbMessage> T deserialize(MvcResult result, Class<T> type) {
    try {
      String data = result.getResponse().getContentAsString();
      return objectMapper.readValue(data, type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResultMatcher isResponse(WebpbMessage expected) {
    return result -> {
      WebpbMessage message = deserialize(result, expected.getClass());
      assertThat(message).usingRecursiveComparison().isEqualTo(expected);
    };
  }

  public ResultMatcher isError(ErrorCode errorCode) {
    return result -> {
      ErrorMessage message = deserialize(result, ErrorMessage.class);
      assertThat(message.getCode()).isEqualTo(errorCode);
    };
  }

  public PagingPb paging(PagingPb pb) {
    return pb.setPage(0).setSize(20).setPages(0).setTotal(0);
  }

  public PagingPb paging(int total, PagingPb pb) {
    return pb.setPage(0).setSize(20).setPages((total + 19) / 20 - 1).setTotal(total);
  }
}
