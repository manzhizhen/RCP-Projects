/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     2011-9-20 14:49:47                           */
/*==============================================================*/


drop table BS_DEPARTMENT cascade constraints;

drop table BS_FUNCMAPPING cascade constraints;

drop table BS_FUNCTION cascade constraints;

drop table BS_PATCH cascade constraints;

drop table BS_PATCHMAPPING cascade constraints;

drop table BS_PC cascade constraints;

drop table BS_PLUGIN cascade constraints;

drop table BS_PLUGINLOG cascade constraints;

drop table BS_ROLE cascade constraints;

drop table BS_ROLEMAPPING cascade constraints;

drop table BS_USER cascade constraints;

drop table BS_USERMAPPING cascade constraints;

drop table BS_WIDGET cascade constraints;

drop table DM_ALGORITHM cascade constraints;

drop table DM_BASEPARA cascade constraints;

drop table DM_CHECK cascade constraints;

drop table DM_CLASSIFYVAL cascade constraints;

drop table DM_DATASET cascade constraints;

drop table DM_FUNCMAPPING cascade constraints;

drop table DM_FUNCTION cascade constraints;

drop table DM_GROUPCONDITION cascade constraints;

drop table DM_INTERCEPTRULL cascade constraints;

drop table DM_REASONCODE cascade constraints;

drop table DM_REFERENCE cascade constraints;

drop table DM_VARIABLE cascade constraints;

drop table DM_VARIABLERESULT cascade constraints;

/*==============================================================*/
/* Table: BS_DEPARTMENT                                         */
/*==============================================================*/
create table BS_DEPARTMENT  (
   BANKORG_ID           NUMBER(10)                      not null,
   DEPARTMENT_ID        VARCHAR2(10)                    not null,
   DEPARTMENT_NAME      VARCHAR2(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_DEPARTMENT primary key (BANKORG_ID, DEPARTMENT_ID)
);

comment on table BS_DEPARTMENT is
'部门表[BS_DEPARTMENT]';

/*==============================================================*/
/* Table: BS_FUNCMAPPING                                        */
/*==============================================================*/
create table BS_FUNCMAPPING  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   FUNCTION_ID          VARCHAR2(10)                    not null,
   WIDGET_ID            VARCHAR2(10)                    not null,
   MAPPING_TYPE         VARCHAR2(1),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_FUNCMAPPING primary key (BANKORG_ID, PC_ID, FUNCTION_ID, WIDGET_ID)
);

comment on table BS_FUNCMAPPING is
'功能控件映射表[BS_FUNCMAPPING]';

/*==============================================================*/
/* Table: BS_FUNCTION                                           */
/*==============================================================*/
create table BS_FUNCTION  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   FUNCTION_ID          VARCHAR2(10)                    not null,
   FUNCTION_NAME        VARCHAR2(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_FUNCTION primary key (BANKORG_ID, PC_ID, FUNCTION_ID)
);

comment on table BS_FUNCTION is
'功能表[BS_FUNCTION]';

/*==============================================================*/
/* Table: BS_PATCH                                              */
/*==============================================================*/
create table BS_PATCH  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   PATCH_ID             VARCHAR2(10)                    not null,
   PLUGIN_ID            VARCHAR2(10),
   PATCH_VER            VARCHAR2(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_PATCH primary key (BANKORG_ID, PC_ID, PATCH_ID)
);

comment on table BS_PATCH is
'补丁表[BS_PATCH]';

/*==============================================================*/
/* Table: BS_PATCHMAPPING                                       */
/*==============================================================*/
create table BS_PATCHMAPPING  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   PATCH_ID             VARCHAR2(10)                    not null,
   PLUGIN_VER           VARCHAR2(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_PATCHMAPPING primary key (BANKORG_ID, PC_ID, PATCH_ID, PLUGIN_VER)
);

comment on table BS_PATCHMAPPING is
'补丁版本映射表[BS_PATCHMAPPING]';

/*==============================================================*/
/* Table: BS_PC                                                 */
/*==============================================================*/
create table BS_PC  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   PC_NAME              VARCHAR2(40),
   IP_ADDR              VARCHAR2(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_PC primary key (BANKORG_ID, PC_ID)
);

comment on table BS_PC is
'电脑表[BS_PC]';

/*==============================================================*/
/* Table: BS_PLUGIN                                             */
/*==============================================================*/
create table BS_PLUGIN  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   PLUGIN_ID            VARCHAR2(10)                    not null,
   PLUGIN_NAME          VARCHAR2(40),
   PLUGIN_VER           VARCHAR2(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_PLUGIN primary key (BANKORG_ID, PC_ID, PLUGIN_ID)
);

comment on table BS_PLUGIN is
'插件表[BS_PLUGIN]';

/*==============================================================*/
/* Table: BS_PLUGINLOG                                          */
/*==============================================================*/
create table BS_PLUGINLOG  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   LOG_SEQ              NUMBER(10)                      not null,
   PLUGIN_ID            VARCHAR2(10),
   PROCESS_TYPE         VARCHAR2(1),
   PROCESS_DATE         DATE,
   SRC_PLUGIN_VER       VARCHAR2(10),
   PLUGIN_VER           VARCHAR2(10),
   PATCH_ID             VARCHAR2(10),
   REPLACE_URL          VARCHAR2(200),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_PLUGINLOG primary key (BANKORG_ID, PC_ID, LOG_SEQ)
);

comment on table BS_PLUGINLOG is
'插件日志表[BS_PLUGINLOG]';

comment on column BS_PLUGINLOG.PROCESS_TYPE is
'A-安装新插件
D-卸载插件
P-补丁升级
R-补丁恢复
';

/*==============================================================*/
/* Table: BS_ROLE                                               */
/*==============================================================*/
create table BS_ROLE  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   ROLE_ID              VARCHAR2(10)                    not null,
   ROLE_NAME            VARCHAR2(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_ROLE primary key (BANKORG_ID, PC_ID, ROLE_ID)
);

comment on table BS_ROLE is
'角色表[BS_ROLE]';

/*==============================================================*/
/* Table: BS_ROLEMAPPING                                        */
/*==============================================================*/
create table BS_ROLEMAPPING  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   ROLE_ID              VARCHAR2(10)                    not null,
   FUNCTION_ID          VARCHAR2(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_ROLEMAPPING primary key (BANKORG_ID, PC_ID, ROLE_ID, FUNCTION_ID)
);

comment on table BS_ROLEMAPPING is
'角色功能映射表[BS_ROLEMAPPING]';

/*==============================================================*/
/* Table: BS_USER                                               */
/*==============================================================*/
create table BS_USER  (
   BANKORG_ID           NUMBER(10)                      not null,
   USER_ID              VARCHAR2(10)                    not null,
   USER_NAME            VARCHAR2(40),
   DEPARTMENT_ID        VARCHAR2(10),
   PASSWORD             VARCHAR2(40),
   USER_STATUS          VARCHAR2(1),
   LASET_LOGGIN_DATE    DATE,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_USER primary key (BANKORG_ID, USER_ID)
);

comment on table BS_USER is
'用户表[BS_USER]';

comment on column BS_USER.USER_STATUS is
'U-新员工，未使用
N-正常
L-离职
D-请假
';

/*==============================================================*/
/* Table: BS_USERMAPPING                                        */
/*==============================================================*/
create table BS_USERMAPPING  (
   BANKORG_ID           NUMBER(10)                      not null,
   USER_ID              VARCHAR2(10)                    not null,
   PC_ID                VARCHAR2(20)                    not null,
   ROLE_ID              VARCHAR2(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_USERMAPPING primary key (BANKORG_ID, USER_ID, PC_ID, ROLE_ID)
);

comment on table BS_USERMAPPING is
'用户角色映射表[BS_USERMAPPING]';

/*==============================================================*/
/* Table: BS_WIDGET                                             */
/*==============================================================*/
create table BS_WIDGET  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   WIDGET_ID            VARCHAR2(10)                    not null,
   WIDGET_NAME          VARCHAR2(40),
   WIDGET_TYPE          VARCHAR2(1),
   PAR_WIDGET_ID        VARCHAR2(10),
   PLUGIN_ID            VARCHAR2(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_BS_WIDGET primary key (BANKORG_ID, PC_ID, WIDGET_ID)
);

comment on table BS_WIDGET is
'控件表[BS_WIDGET]';

comment on column BS_WIDGET.WIDGET_TYPE is
'B-按钮
R-单选框
C-复选框
D-复选框
T-文本框
W-窗体
E-下拉菜单按钮
F-工具栏按钮
';

comment on column BS_WIDGET.PAR_WIDGET_ID is
'如果没有父级控件，则为999999999';

/*==============================================================*/
/* Table: DM_ALGORITHM                                          */
/*==============================================================*/
create table DM_ALGORITHM  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   VARIABLE_ID          VARCHAR2(10)                    not null,
   ALGORITHM_LINE       VARCHAR2(200),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_ALGORITHM primary key (BANKORG_ID, PC_ID, VARIABLE_ID)
);

comment on table DM_ALGORITHM is
'算法表[DM_ALGORITHM]';

/*==============================================================*/
/* Table: DM_BASEPARA                                           */
/*==============================================================*/
create table DM_BASEPARA  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   VAL_CHK_FAIL_REASON  VARCHAR2(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_BASEPARA primary key (BANKORG_ID, PC_ID)
);

comment on table DM_BASEPARA is
'基础参数表[DM_BASEPARA]';

/*==============================================================*/
/* Table: DM_CHECK                                              */
/*==============================================================*/
create table DM_CHECK  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   CHECK_OBJ_TYPE       VARCHAR2(1)                     not null,
   CHECK_OBJ_ID         VARCHAR2(10)                    not null,
   DATASET_ID           VARCHAR2(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_CHECK primary key (BANKORG_ID, PC_ID, CHECK_OBJ_TYPE, CHECK_OBJ_ID, DATASET_ID)
);

comment on table DM_CHECK is
'检查表[DM_CHECK]';

/*==============================================================*/
/* Table: DM_CLASSIFYVAL                                        */
/*==============================================================*/
create table DM_CLASSIFYVAL  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   VARIABLE_ID          VARCHAR2(10)                    not null,
   CLASSIFY_VAL_SEQ     NUMBER(10)                      not null,
   CLASSIFY_VAL         VARCHAR2(40),
   CLASSIFY_VAL_DESC    VARCHAR2(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_CLASSIFYVAL primary key (BANKORG_ID, PC_ID, VARIABLE_ID, CLASSIFY_VAL_SEQ)
);

comment on table DM_CLASSIFYVAL is
'分类变量值表[DM_CLASSIFYVAL]';

/*==============================================================*/
/* Table: DM_DATASET                                            */
/*==============================================================*/
create table DM_DATASET  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   DATASET_ID           VARCHAR2(10)                    not null,
   DATASET_NAME         VARCHAR2(40),
   DATASET_LINE         BLOB,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_DATASET primary key (BANKORG_ID, PC_ID, DATASET_ID)
);

comment on table DM_DATASET is
'数据集表[DM_DATASET]';

/*==============================================================*/
/* Table: DM_FUNCMAPPING                                        */
/*==============================================================*/
create table DM_FUNCMAPPING  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   FUNCTION_ID          VARCHAR2(10)                    not null,
   INPUT_VAL            VARCHAR2(10)                    not null,
   INPUT_TYPE           VARCHAR2(1),
   FUNCTION_LINE        VARCHAR2(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_FUNCMAPPING primary key (BANKORG_ID, PC_ID, FUNCTION_ID, INPUT_VAL)
);

comment on table DM_FUNCMAPPING is
'函数映射[DM_FUNCMAPPING]';

comment on column DM_FUNCMAPPING.INPUT_TYPE is
'N-字符型
C-数字型
D-日期型
';

/*==============================================================*/
/* Table: DM_FUNCTION                                           */
/*==============================================================*/
create table DM_FUNCTION  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   FUNCTION_ID          VARCHAR2(10)                    not null,
   FUNCTION_NAME        VARCHAR2(40),
   OUTPUT_TYPE          VARCHAR2(1),
   FUNCTION_LINE        VARCHAR2(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_FUNCTION primary key (BANKORG_ID, PC_ID, FUNCTION_ID)
);

comment on table DM_FUNCTION is
'函数[DM_FUNCTION]';

comment on column DM_FUNCTION.OUTPUT_TYPE is
'N-字符型
C-数字型
D-日期型
';

/*==============================================================*/
/* Table: DM_GROUPCONDITION                                     */
/*==============================================================*/
create table DM_GROUPCONDITION  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   GRP_CON_ID           VARCHAR2(10)                    not null,
   GRP_CON_NAME         VARCHAR2(40),
   GRP_CON_LINE         VARCHAR2(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_GROUPCONDITION primary key (BANKORG_ID, PC_ID, GRP_CON_ID)
);

comment on table DM_GROUPCONDITION is
'入组条件[DM_GROUPCONDITION]';

/*==============================================================*/
/* Table: DM_INTERCEPTRULL                                      */
/*==============================================================*/
create table DM_INTERCEPTRULL  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   VARIABLE_ID          VARCHAR2(10)                    not null,
   INTERCEPT_CHANNEL    VARCHAR2(1),
   CHANNEL_NODE         VARCHAR2(100),
   INTERCEPT_PATH       VARCHAR2(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_INTERCEPTRULL primary key (BANKORG_ID, PC_ID, VARIABLE_ID)
);

comment on table DM_INTERCEPTRULL is
'外部变量截取规则[DM_INTERCEPTRULL]';

comment on column DM_INTERCEPTRULL.INTERCEPT_CHANNEL is
'M-报文类型
S-存储类型
';

/*==============================================================*/
/* Table: DM_REASONCODE                                         */
/*==============================================================*/
create table DM_REASONCODE  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   REASON_CODE          VARCHAR2(10)                    not null,
   REASON_TYPE          VARCHAR2(1),
   REASON_DESC          VARCHAR2(50),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_REASONCODE primary key (BANKORG_ID, PC_ID, REASON_CODE)
);

comment on table DM_REASONCODE is
'原因代码表[DM_REASONCODE]';

comment on column DM_REASONCODE.REASON_TYPE is
'S-系统原因代码
B-业务原因代码
';

/*==============================================================*/
/* Table: DM_REFERENCE                                          */
/*==============================================================*/
create table DM_REFERENCE  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   SRC_REF_TYPE         VARCHAR2(1)                     not null,
   REF_OBJ_ID           VARCHAR2(10)                    not null,
   REF_TYPE             VARCHAR2(1)                     not null,
   OBJ_ID               VARCHAR2(10)                    not null,
   REF_COUNTS           NUMBER(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_REFERENCE primary key (BANKORG_ID, PC_ID, SRC_REF_TYPE, REF_OBJ_ID, REF_TYPE, OBJ_ID)
);

comment on table DM_REFERENCE is
'引用表[DM_REFERENCE]';

comment on column DM_REFERENCE.SRC_REF_TYPE is
'E-外部变量
I-内部变量
D-抽样数据集
P-政策
S-策略
G-入组条件
F-函数
C-评分卡
A-统计分析
B-原因代码
H-结果代码

';

comment on column DM_REFERENCE.REF_TYPE is
'E-外部变量
I-内部变量
D-抽样数据集
P-政策
S-策略
G-入组条件
F-函数
C-评分卡
A-统计分析
B-原因代码
H-结果代码
';

/*==============================================================*/
/* Table: DM_VARIABLE                                           */
/*==============================================================*/
create table DM_VARIABLE  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   VARIABLE_ID          VARCHAR2(10)                    not null,
   VARIABLE_DESC        VARCHAR2(100),
   VARIABLE_CLASS       VARCHAR2(1),
   VARIABLE_TYPE        VARCHAR2(1),
   DATE_FORMAT          VARCHAR2(20),
   VAL_LENGTH           NUMBER(10),
   VAL_PRECISION        NUMBER(10),
   VARIABLE_STATUS      VARCHAR2(1),
   CLASSIFY_IND         VARCHAR2(1),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_VARIABLE primary key (BANKORG_ID, PC_ID, VARIABLE_ID)
);

comment on table DM_VARIABLE is
'变量表[DM_VARIABLE]';

comment on column DM_VARIABLE.VARIABLE_CLASS is
'E-外部变量
I-内部变量
';

comment on column DM_VARIABLE.VARIABLE_TYPE is
'D-日期型
S-字符型
N-数字型
';

comment on column DM_VARIABLE.VARIABLE_STATUS is
'D-算法未定义
U-未校验
N-已校验
C-已计算
A-归档

';

comment on column DM_VARIABLE.CLASSIFY_IND is
'Y-是
N-不是
';

/*==============================================================*/
/* Table: DM_VARIABLERESULT                                     */
/*==============================================================*/
create table DM_VARIABLERESULT  (
   BANKORG_ID           NUMBER(10)                      not null,
   PC_ID                VARCHAR2(20)                    not null,
   DATASET_ID           VARCHAR2(10)                    not null,
   VARIABLE_ID          VARCHAR2(10)                    not null,
   RESULT               VARCHAR2(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR2(64),
   VERSION              NUMBER(8),
   constraint PK_DM_VARIABLERESULT primary key (BANKORG_ID, PC_ID, DATASET_ID, VARIABLE_ID)
);

comment on table DM_VARIABLERESULT is
'变量计算结果表[DM_VARIABLERESULT]';

