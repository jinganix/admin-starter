package io.github.jinganix.admin.starter.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.jinganix.admin.starter.adm.overview.OverviewMapper;
import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.helper.auth.token.TokenService;
import io.github.jinganix.admin.starter.helper.data.AbstractEntity;
import io.github.jinganix.admin.starter.proto.lib.pageable.PagingPb;
import io.github.jinganix.admin.starter.proto.service.enumeration.ErrorCode;
import io.github.jinganix.admin.starter.proto.service.error.ErrorMessage;
import io.github.jinganix.admin.starter.schema.Tables;
import io.github.jinganix.admin.starter.sys.audit.AuditMapper;
import io.github.jinganix.admin.starter.sys.audit.model.Audit;
import io.github.jinganix.admin.starter.sys.auth.model.AdminUserIdentity;
import io.github.jinganix.admin.starter.sys.permission.PermissionMapper;
import io.github.jinganix.admin.starter.sys.permission.model.Permission;
import io.github.jinganix.admin.starter.sys.role.RoleMapper;
import io.github.jinganix.admin.starter.sys.role.model.Role;
import io.github.jinganix.admin.starter.sys.role.model.RolePermission;
import io.github.jinganix.admin.starter.sys.user.UserMapper;
import io.github.jinganix.admin.starter.sys.user.model.User;
import io.github.jinganix.admin.starter.sys.user.model.UserRole;
import io.github.jinganix.webpb.runtime.WebpbMessage;
import io.github.jinganix.webpb.runtime.WebpbUtils;
import io.github.jinganix.webpb.runtime.common.InQuery;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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
public class TestHelper {

  private static final Set<Table<?>> TABLES = TestDataUtils.resolveTables(Tables.class);

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

  @Autowired(required = false)
  private CacheManager cacheManager;

  @Autowired private DSLContext dsl;

  @Autowired private UserMapper userMapper;

  @Autowired private RoleMapper roleMapper;

  @Autowired private PermissionMapper permissionMapper;

  @Autowired private AuditMapper auditMapper;

  @Autowired private OverviewMapper overviewMapper;

  public void clearAll() {
    if (cacheManager != null) {
      cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }
    truncateTables();
  }

  public void truncateTables() {
    dsl.batch(
            TABLES.stream()
                .filter(table -> dsl.fetchCount(table) > 0)
                .map(table -> dsl.truncate(table))
                .toList())
        .execute();
  }

  public void insertRecords(TableRecord<?>... records) {
    for (TableRecord<?> record : records) {
      dsl.insertInto(record.getTable()).set(record).execute();
    }
  }

  public void insertEntities(AbstractEntity... entities) {
    for (AbstractEntity entity : entities) {
      TableRecord<?> record = toRecord(entity);
      dsl.insertInto(record.getTable()).set(record).execute();
    }
  }

  private TableRecord<?> toRecord(AbstractEntity entity) {
    if (entity instanceof User user) {
      return userMapper.toRecord(user);
    }
    if (entity instanceof AdminUserIdentity identity) {
      return userMapper.toRecord(identity);
    }
    if (entity instanceof UserRole userRole) {
      return userMapper.toRecord(userRole);
    }
    if (entity instanceof Role role) {
      return roleMapper.toRecord(role);
    }
    if (entity instanceof RolePermission rolePermission) {
      return roleMapper.toRecord(rolePermission);
    }
    if (entity instanceof Permission permission) {
      return permissionMapper.toRecord(permission);
    }
    if (entity instanceof Audit audit) {
      return auditMapper.toRecord(audit);
    }
    if (entity instanceof Overview overview) {
      return overviewMapper.toRecord(overview);
    }
    throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass());
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
    return request(token, message);
  }

  public ResultActions request(String token, WebpbMessage message) throws Exception {
    HttpMethod method = HttpMethod.valueOf(message.webpbMeta().getMethod());
    return mockMvc.perform(
        MockMvcRequestBuilders.request(method, WebpbUtils.formatUrl(message))
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebpbUtils.serialize(message)));
  }

  public ResultActions requestRawJson(Long userId, String method, String path, String content)
      throws Exception {
    String token = tokenService.generate(userId);
    return mockMvc.perform(
        MockMvcRequestBuilders.request(HttpMethod.valueOf(method), path)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content));
  }

  public ResultActions request(Long userId, WebpbMessage message, Map<String, String> queryParams)
      throws Exception {
    HttpMethod method = HttpMethod.valueOf(message.webpbMeta().getMethod());
    String token = tokenService.generate(userId);
    var requestBuilder =
        MockMvcRequestBuilders.request(method, WebpbUtils.formatUrl(message))
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(WebpbUtils.serialize(message));
    queryParams.forEach((key, value) -> requestBuilder.queryParam(key, value));
    return mockMvc.perform(requestBuilder);
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

  public ResultActions expectError(ResultActions actions, ErrorCode errorCode) throws Exception {
    return actions.andExpect(status().isBadRequest()).andExpect(isError(errorCode));
  }

  public PagingPb paging(PagingPb pb) {
    return pb.setPage(0).setSize(20).setPages(0).setTotal(0);
  }

  public PagingPb paging(int total, PagingPb pb) {
    return pb.setPage(0).setSize(20).setPages((total + 19) / 20 - 1).setTotal(total);
  }
}
