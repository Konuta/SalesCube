SELECT
    CATEGORY_ID
    ,GROUP_NAME
    ,CATEGORY_NAME
    ,CATEGORY_ADD
    ,CATEGORY_UPD
    ,CATEGORY_DEL
    ,CATEGORY_DSP
    ,CATEGORY_DATA_TYPE
    ,CATEGORY_STR_SIZE
    ,CATEGORY_CODE_SIZE
    ,CATEGORY_TITLE
    ,CRE_FUNC
    ,CRE_DATETM
    ,CRE_USER
    ,UPD_FUNC
    ,UPD_DATETM
    ,UPD_USER
FROM
	CATEGORY_MST_/*$domainId*/
WHERE
	GROUP_NAME=/*groupName*/
	AND
	(CATEGORY_ADD=1 OR CATEGORY_UPD=1 OR CATEGORY_DEL=1)
ORDER BY
	/*sortOrderColumn*/