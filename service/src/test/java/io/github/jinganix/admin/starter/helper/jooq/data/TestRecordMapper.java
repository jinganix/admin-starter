package io.github.jinganix.admin.starter.helper.jooq.data;

public class TestRecordMapper {

  public TestEntity toEntity(TestRecord record) {
    if (record == null) {
      return null;
    }
    return new TestEntity()
        .setId(record.getId())
        .setGroupId(record.getGroupId())
        .setData(record.getData());
  }
}
