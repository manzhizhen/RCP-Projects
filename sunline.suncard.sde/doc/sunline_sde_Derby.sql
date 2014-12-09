/*==============================================================*/
/* DBMS name:      Derby                                        */
/* Created on:     2011-9-20 14:49:47                           */
/*==============================================================*/


drop table BS_DEPARTMENT;

drop table BS_FUNCMAPPING;

drop table BS_FUNCTION;

drop table BS_PATCH;

drop table BS_PATCHMAPPING;

drop table BS_PC;

drop table BS_PLUGIN;

drop table BS_PLUGINLOG;

drop table BS_ROLE;

drop table BS_ROLEMAPPING;

drop table BS_USER;

drop table BS_USERMAPPING;

drop table BS_WIDGET;

drop table DM_ALGORITHM;

drop table DM_BASEPARA;

drop table DM_CHECK;

drop table DM_CLASSIFYVAL;

drop table DM_DATASET;

drop table DM_FUNCMAPPING;

drop table DM_FUNCTION;

drop table DM_GROUPCONDITION;

drop table DM_INTERCEPTRULL;

drop table DM_REASONCODE;

drop table DM_REFERENCE;

drop table DM_VARIABLE;

drop table DM_VARIABLERESULT;

/*==============================================================*/
/* Table: BS_DEPARTMENT                                         */
/*==============================================================*/
create table BS_DEPARTMENT  (
   BANKORG_ID           int                      not null,
   DEPARTMENT_ID        VARCHAR(10)                    not null,
   DEPARTMENT_NAME      VARCHAR(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_DEPARTMENT primary key (BANKORG_ID, DEPARTMENT_ID)
);



/*==============================================================*/
/* Table: BS_FUNCMAPPING                                        */
/*==============================================================*/
create table BS_FUNCMAPPING  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   FUNCTION_ID          VARCHAR(10)                    not null,
   WIDGET_ID            VARCHAR(10)                    not null,
   MAPPING_TYPE         VARCHAR(1),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_FUNCMAPPING primary key (BANKORG_ID, PC_ID, FUNCTION_ID, WIDGET_ID)
);


/*==============================================================*/
/* Table: BS_FUNCTION                                           */
/*==============================================================*/
create table BS_FUNCTION  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   FUNCTION_ID          VARCHAR(10)                    not null,
   FUNCTION_NAME        VARCHAR(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_FUNCTION primary key (BANKORG_ID, PC_ID, FUNCTION_ID)
);


/*==============================================================*/
/* Table: BS_PATCH                                              */
/*==============================================================*/
create table BS_PATCH  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   PATCH_ID             VARCHAR(10)                    not null,
   PLUGIN_ID            VARCHAR(10),
   PATCH_VER            VARCHAR(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_PATCH primary key (BANKORG_ID, PC_ID, PATCH_ID)
);


/*==============================================================*/
/* Table: BS_PATCHMAPPING                                       */
/*==============================================================*/
create table BS_PATCHMAPPING  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   PATCH_ID             VARCHAR(10)                    not null,
   PLUGIN_VER           VARCHAR(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_PATCHMAPPING primary key (BANKORG_ID, PC_ID, PATCH_ID, PLUGIN_VER)
);


/*==============================================================*/
/* Table: BS_PC                                                 */
/*==============================================================*/
create table BS_PC  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   PC_NAME              VARCHAR(40),
   IP_ADDR              VARCHAR(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_PC primary key (BANKORG_ID, PC_ID)
);


/*==============================================================*/
/* Table: BS_PLUGIN                                             */
/*==============================================================*/
create table BS_PLUGIN  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   PLUGIN_ID            VARCHAR(10)                    not null,
   PLUGIN_NAME          VARCHAR(40),
   PLUGIN_VER           VARCHAR(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_PLUGIN primary key (BANKORG_ID, PC_ID, PLUGIN_ID)
);


/*==============================================================*/
/* Table: BS_PLUGINLOG                                          */
/*==============================================================*/
create table BS_PLUGINLOG  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   LOG_SEQ              int                      not null,
   PLUGIN_ID            VARCHAR(10),
   PROCESS_TYPE         VARCHAR(1),
   PROCESS_DATE         DATE,
   SRC_PLUGIN_VER       VARCHAR(10),
   PLUGIN_VER           VARCHAR(10),
   PATCH_ID             VARCHAR(10),
   REPLACE_URL          VARCHAR(200),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_PLUGINLOG primary key (BANKORG_ID, PC_ID, LOG_SEQ)
);

/*==============================================================*/
/* Table: BS_ROLE                                               */
/*==============================================================*/
create table BS_ROLE  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   ROLE_ID              VARCHAR(10)                    not null,
   ROLE_NAME            VARCHAR(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_ROLE primary key (BANKORG_ID, PC_ID, ROLE_ID)
);


/*==============================================================*/
/* Table: BS_ROLEMAPPING                                        */
/*==============================================================*/
create table BS_ROLEMAPPING  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   ROLE_ID              VARCHAR(10)                    not null,
   FUNCTION_ID          VARCHAR(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_ROLEMAPPING primary key (BANKORG_ID, PC_ID, ROLE_ID, FUNCTION_ID)
);


/*==============================================================*/
/* Table: BS_USER                                               */
/*==============================================================*/
create table BS_USER  (
   BANKORG_ID           int                      not null,
   USER_ID              VARCHAR(10)                    not null,
   USER_NAME            VARCHAR(40),
   DEPARTMENT_ID        VARCHAR(10),
   PASSWORD             VARCHAR(40),
   USER_STATUS          VARCHAR(1),
   LASET_LOGGIN_DATE    DATE,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_USER primary key (BANKORG_ID, USER_ID)
);


/*==============================================================*/
/* Table: BS_USERMAPPING                                        */
/*==============================================================*/
create table BS_USERMAPPING  (
   BANKORG_ID           int                      not null,
   USER_ID              VARCHAR(10)                    not null,
   PC_ID                VARCHAR(20)                    not null,
   ROLE_ID              VARCHAR(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_USERMAPPING primary key (BANKORG_ID, USER_ID, PC_ID, ROLE_ID)
);


/*==============================================================*/
/* Table: BS_WIDGET                                             */
/*==============================================================*/
create table BS_WIDGET  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   WIDGET_ID            VARCHAR(10)                    not null,
   WIDGET_NAME          VARCHAR(40),
   WIDGET_TYPE          VARCHAR(1),
   PAR_WIDGET_ID        VARCHAR(10),
   PLUGIN_ID            VARCHAR(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_BS_WIDGET primary key (BANKORG_ID, PC_ID, WIDGET_ID)
);


/*==============================================================*/
/* Table: DM_ALGORITHM                                          */
/*==============================================================*/
create table DM_ALGORITHM  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   VARIABLE_ID          VARCHAR(10)                    not null,
   ALGORITHM_LINE       VARCHAR(200),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_ALGORITHM primary key (BANKORG_ID, PC_ID, VARIABLE_ID)
);


/*==============================================================*/
/* Table: DM_BASEPARA                                           */
/*==============================================================*/
create table DM_BASEPARA  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   VAL_CHK_FAIL_REASON  VARCHAR(10),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_BASEPARA primary key (BANKORG_ID, PC_ID)
);


/*==============================================================*/
/* Table: DM_CHECK                                              */
/*==============================================================*/
create table DM_CHECK  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   CHECK_OBJ_TYPE       VARCHAR(1)                     not null,
   CHECK_OBJ_ID         VARCHAR(10)                    not null,
   DATASET_ID           VARCHAR(10)                    not null,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_CHECK primary key (BANKORG_ID, PC_ID, CHECK_OBJ_TYPE, CHECK_OBJ_ID, DATASET_ID)
);


/*==============================================================*/
/* Table: DM_CLASSIFYVAL                                        */
/*==============================================================*/
create table DM_CLASSIFYVAL  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   VARIABLE_ID          VARCHAR(10)                    not null,
   CLASSIFY_VAL_SEQ     int                      not null,
   CLASSIFY_VAL         VARCHAR(40),
   CLASSIFY_VAL_DESC    VARCHAR(40),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_CLASSIFYVAL primary key (BANKORG_ID, PC_ID, VARIABLE_ID, CLASSIFY_VAL_SEQ)
);


/*==============================================================*/
/* Table: DM_DATASET                                            */
/*==============================================================*/
create table DM_DATASET  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   DATASET_ID           VARCHAR(10)                    not null,
   DATASET_NAME         VARCHAR(40),
   DATASET_LINE         BLOB,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_DATASET primary key (BANKORG_ID, PC_ID, DATASET_ID)
);


/*==============================================================*/
/* Table: DM_FUNCMAPPING                                        */
/*==============================================================*/
create table DM_FUNCMAPPING  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   FUNCTION_ID          VARCHAR(10)                    not null,
   INPUT_VAL            VARCHAR(10)                    not null,
   INPUT_TYPE           VARCHAR(1),
   FUNCTION_LINE        VARCHAR(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_FUNCMAPPING primary key (BANKORG_ID, PC_ID, FUNCTION_ID, INPUT_VAL)
);


/*==============================================================*/
/* Table: DM_FUNCTION                                           */
/*==============================================================*/
create table DM_FUNCTION  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   FUNCTION_ID          VARCHAR(10)                    not null,
   FUNCTION_NAME        VARCHAR(40),
   OUTPUT_TYPE          VARCHAR(1),
   FUNCTION_LINE        VARCHAR(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_FUNCTION primary key (BANKORG_ID, PC_ID, FUNCTION_ID)
);


/*==============================================================*/
/* Table: DM_GROUPCONDITION                                     */
/*==============================================================*/
create table DM_GROUPCONDITION  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   GRP_CON_ID           VARCHAR(10)                    not null,
   GRP_CON_NAME         VARCHAR(40),
   GRP_CON_LINE         VARCHAR(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_GROUPCONDITION primary key (BANKORG_ID, PC_ID, GRP_CON_ID)
);


/*==============================================================*/
/* Table: DM_INTERCEPTRULL                                      */
/*==============================================================*/
create table DM_INTERCEPTRULL  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   VARIABLE_ID          VARCHAR(10)                    not null,
   INTERCEPT_CHANNEL    VARCHAR(1),
   CHANNEL_NODE         VARCHAR(100),
   INTERCEPT_PATH       VARCHAR(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_INTERCEPTRULL primary key (BANKORG_ID, PC_ID, VARIABLE_ID)
);

/*==============================================================*/
/* Table: DM_REASONCODE                                         */
/*==============================================================*/
create table DM_REASONCODE  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   REASON_CODE          VARCHAR(10)                    not null,
   REASON_TYPE          VARCHAR(1),
   REASON_DESC          VARCHAR(50),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_REASONCODE primary key (BANKORG_ID, PC_ID, REASON_CODE)
);

/*==============================================================*/
/* Table: DM_REFERENCE                                          */
/*==============================================================*/
create table DM_REFERENCE  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   SRC_REF_TYPE         VARCHAR(1)                     not null,
   REF_OBJ_ID           VARCHAR(10)                    not null,
   REF_TYPE             VARCHAR(1)                     not null,
   OBJ_ID               VARCHAR(10)                    not null,
   REF_COUNTS           int,
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_REFERENCE primary key (BANKORG_ID, PC_ID, SRC_REF_TYPE, REF_OBJ_ID, REF_TYPE, OBJ_ID)
);

/*==============================================================*/
/* Table: DM_VARIABLE                                           */
/*==============================================================*/
create table DM_VARIABLE  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   VARIABLE_ID          VARCHAR(10)                    not null,
   VARIABLE_DESC        VARCHAR(100),
   VARIABLE_CLASS       VARCHAR(1),
   VARIABLE_TYPE        VARCHAR(1),
   DATE_FORMAT          VARCHAR(20),
   VAL_LENGTH           int,
   VAL_PRECISION        int,
   VARIABLE_STATUS      VARCHAR(1),
   CLASSIFY_IND         VARCHAR(1),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_VARIABLE primary key (BANKORG_ID, PC_ID, VARIABLE_ID)
);

/*==============================================================*/
/* Table: DM_VARIABLERESULT                                     */
/*==============================================================*/
create table DM_VARIABLERESULT  (
   BANKORG_ID           int                      not null,
   PC_ID                VARCHAR(20)                    not null,
   DATASET_ID           VARCHAR(10)                    not null,
   VARIABLE_ID          VARCHAR(10)                    not null,
   RESULT               VARCHAR(100),
   MODI_DATE            DATE,
   MODI_USER            VARCHAR(64),
   VERSION              int,
   constraint PK_DM_VARIABLERESULT primary key (BANKORG_ID, PC_ID, DATASET_ID, VARIABLE_ID)
);


