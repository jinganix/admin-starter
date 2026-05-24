package io.github.jinganix.admin.starter.adm.overview.repository;

import static io.github.jinganix.admin.starter.tests.TestConst.MIN_TIMESTAMP;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_1;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_2;
import static io.github.jinganix.admin.starter.tests.TestConst.UID_3;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.admin.starter.adm.overview.model.Overview;
import io.github.jinganix.admin.starter.tests.SpringBootIntegrationTests;
import io.github.jinganix.admin.starter.tests.TestHelper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("OverviewRepository")
class OverviewRepositoryTest extends SpringBootIntegrationTests {

  @Autowired TestHelper testHelper;

  @Autowired OverviewRepository overviewRepository;

  @BeforeEach
  void setup() {
    testHelper.clearAll();
  }

  @Test
  @DisplayName("should return false when month not inserted")
  void shouldReturnFalseWhenMonthNotInserted() {
    // Given
    LocalDate month = LocalDate.of(2026, 5, 1);

    // When
    boolean exists = overviewRepository.existsByMonth(month);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("should return true when month inserted")
  void shouldReturnTrueWhenMonthInserted() {
    // Given
    LocalDate month = LocalDate.of(2026, 5, 1);
    overviewRepository.insert(overview(UID_1, month));

    // When
    boolean exists = overviewRepository.existsByMonth(month);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("should return only older months in desc order when mixed months")
  void shouldReturnOnlyOlderMonthsInDescOrderWhenMixedMonths() {
    // Given
    LocalDate cutoff = LocalDate.of(2026, 5, 1);
    LocalDate olderMonth1 = LocalDate.of(2026, 3, 1);
    LocalDate olderMonth2 = LocalDate.of(2026, 4, 1);
    LocalDate sameMonth = LocalDate.of(2026, 5, 1);
    overviewRepository.insert(overview(UID_1, olderMonth1));
    overviewRepository.insert(overview(UID_2, olderMonth2));
    overviewRepository.insert(overview(UID_3, sameMonth));

    // When
    List<Overview> overviews = overviewRepository.findAllByMonthBefore(cutoff);

    // Then
    assertThat(overviews).extracting(Overview::getMonth).containsExactly(olderMonth2, olderMonth1);
  }

  @Test
  @DisplayName("should increment all counters when existing overview")
  void shouldIncrementAllCountersWhenExistingOverview() {
    // Given
    LocalDate month = LocalDate.of(2026, 5, 1);
    overviewRepository.insert(overview(UID_1, month));

    // When
    overviewRepository.incrementApiGet(month, 2);
    overviewRepository.incrementApiPost(month, 3);
    overviewRepository.incrementUserCreated(month, 4);
    overviewRepository.incrementUserDeleted(month, 5);
    overviewRepository.incrementRoleCreated(month, 6);
    overviewRepository.incrementRoleDeleted(month, 7);
    overviewRepository.incrementPermissionCreated(month, 8);
    overviewRepository.incrementPermissionDeleted(month, 9);
    Overview entity = overviewRepository.findAllByMonthBefore(month.plusMonths(1)).getFirst();

    // Then
    assertThat(entity.getApiGet()).isEqualTo(2);
    assertThat(entity.getApiPost()).isEqualTo(3);
    assertThat(entity.getUserCreated()).isEqualTo(4);
    assertThat(entity.getUserDeleted()).isEqualTo(5);
    assertThat(entity.getRoleCreated()).isEqualTo(6);
    assertThat(entity.getRoleDeleted()).isEqualTo(7);
    assertThat(entity.getPermissionCreated()).isEqualTo(8);
    assertThat(entity.getPermissionDeleted()).isEqualTo(9);
  }

  private Overview overview(long id, LocalDate month) {
    return (Overview)
        new Overview()
            .setId(id)
            .setMonth(month)
            .setCreatedAt(MIN_TIMESTAMP)
            .setUpdatedAt(MIN_TIMESTAMP);
  }
}
