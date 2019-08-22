create table if not exists Dummy
(
    Id         int          not null,
    Code       nvarchar(3)  not null,
    Name       nvarchar(64) not null,
    Created    datetime     not null default current_timestamp(),
    CreatedBy  nvarchar(64) not null default '*AUTO*',
    Modified   datetime     not null default current_timestamp(),
    ModifiedBy nvarchar(64) not null default '*AUTO*',
    constraint PK_Dummy
        primary key (Id)
)