package io.github.jinganix.admin.starter.adm.overview.handler;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.adm.overview.OverviewMapper;
import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.proto.adm.overview.OverviewListResponse;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("OverviewRetrieveHandler")
class OverviewRetrieveHandlerTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired OverviewRetrieveHandler overviewRetrieveHandler;

  @Autowired OverviewMapper overviewMapper;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return empty list when no overviews")
  void shouldReturnEmptyListWhenNoOverviews() {
    // Given / When
    OverviewListResponse response = overviewRetrieveHandler.handle();

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new OverviewListResponse(emptyList()));
  }

  @Test
  @DisplayName("should return only eligible records when overviews before cutoff")
  void shouldReturnOnlyEligibleRecordsWhenOverviewsBeforeCutoff() {
    // Given
    LocalDate cutoff = LocalDate.now().withDayOfMonth(2);
    LocalDate includedMonth = cutoff.minusMonths(1).withDayOfMonth(1);
    LocalDate excludedMonth = cutoff.plusMonths(1).withDayOfMonth(1);
    Overview included =
        (Overview)
            new Overview()
                .setId(UID_1)
                .setMonth(includedMonth)
                .setCreatedAt(MIN_TIMESTAMP)
                .setUpdatedAt(MIN_TIMESTAMP);
    Overview excluded =
        (Overview)
            new Overview()
                .setId(UID_2)
                .setMonth(excludedMonth)
                .setCreatedAt(MIN_TIMESTAMP)
                .setUpdatedAt(MIN_TIMESTAMP);
    testHelper.insertEntities(included, excluded);

    // When
    OverviewListResponse response = overviewRetrieveHandler.handle();

    // Then
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(new OverviewListResponse(List.of(overviewMapper.overviewPb(included))));
  }
}
