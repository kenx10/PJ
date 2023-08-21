create table common_events (
    id BIGINT not null primary key,
    instant TIMESTAMP,
    data VARBINARY
    )


UPSERT INTO common_events VALUES(1,'2002-05-30T09:30:10.5','sdfsdfs')