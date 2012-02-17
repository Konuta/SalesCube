SELECT
	HIST_ID,
	ACTION_TYPE,
	CUSTOMER_CODE,
	CUSTOMER_NAME,
	CUSTOMER_KANA,
	CUSTOMER_OFFICE_NAME,
	CUSTOMER_OFFICE_KANA,
	CUSTOMER_ABBR,
	CUSTOMER_DEPT_NAME,
	CUSTOMER_ZIP_CODE,
	CUSTOMER_ADDRESS_1,
	CUSTOMER_ADDRESS_2,

	CUSTOMER_PC_NAME,
	CUSTOMER_PC_KANA,
	CUSTOMER_PC_PRE_CATEGORY CUSTOMER_PC_PRE_CATEGORY_CDX,
	CATT4.CATEGORY_CODE_NAME CUSTOMER_PC_PRE_CATEGORY_NM,
	CUSTOMER_DEPT_NAME,
	CUSTOMER_PC_POST,
	CUSTOMER_TEL,
	CUSTOMER_FAX,
	CUSTOMER_EMAIL,

	CUSTOMER_RANK_CATEGORY CUSTOMER_RANK_CATEGORY_CDX,
	CR.RANK_NAME,
	CASE WHEN CUSTOMER_UPD_FLAG = '1' THEN '有'
          ELSE '無' END CUSTOMER_UPD_FLAG,
	CUSTOMER_RO_CATEGORY CUSTOMER_RO_CATEGORY_CDX,
	CATT5.CATEGORY_CODE_NAME CUSTOMER_RO_CATEGORY_NM,
	MAX_CREDIT_LIMIT,
	CUSTOMER_BUSINESS_CATEGORY CUSTOMER_BUSINESS_CATEGORY_CDX,
	CATT6.CATEGORY_CODE_NAME   CUSTOMER_BUSINESS_CATEGORY_NM,
	CUSTOMER_JOB_CATEGORY    CUSTOMER_JOB_CATEGORY_CDX,
	CATT7.CATEGORY_CODE_NAME CUSTOMER_JOB_CATEGORY_NM,
	TAX_FRACT_CATEGORY       TAX_FRACT_CATEGORY_CDX,
	CATT8.CATEGORY_CODE_NAME TAX_FRACT_CATEGORY_NM,
	PRICE_FRACT_CATEGORY     PRICE_FRACT_CATEGORY_CDX,
	CATT9.CATEGORY_CODE_NAME PRICE_FRACT_CATEGORY_NM,
	TAX_SHIFT_CATEGORY       TAX_SHIFT_CATEGORY_CDX,
	CATT2.CATEGORY_CODE_NAME TAX_SHIFT_CATEGORY_NM,
	LAST_CUTOFF_DATE,

	SALES_CM_CATEGORY        SALES_CM_CATEGORY_CDX,
	CATT3.CATEGORY_CODE_NAME SALES_CM_CATEGORY_NM,
	CONCAT(CUTOFF_GROUP, PAYBACK_CYCLE_CATEGORY) CUTOFF_GROUP_CDX,
	CATT.CATEGORY_CODE_NAME,
	PAYBACK_TYPE_CATEGORY     PAYBACK_TYPE_CATEGORY_CDX,
	CATT10.CATEGORY_CODE_NAME PAYBACK_TYPE_CATEGORY_NM,
	CASE WHEN TEMP_DELIVERY_SLIP_FLAG = '0' THEN '不可'
          ELSE '可' END TEMP_DELIVERY_SLIP_FLAG,

	PAYMENT_NAME,
	HIST.REMARKS REMARKS,
	COMMENT_DATA,
	HIST.REC_DATETM UPD_DATETM
FROM
	CUSTOMER_MST_HIST_/*$domainId*/ HIST
		LEFT OUTER JOIN CUSTOMER_RANK_MST_/*$domainId*/ CR
			ON CUSTOMER_RANK_CATEGORY = CR.RANK_CODE
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT
			ON CONCAT(CUTOFF_GROUP, PAYBACK_CYCLE_CATEGORY) = CATT.CATEGORY_CODE AND CATT.CATEGORY_ID=11
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT2
			ON TAX_SHIFT_CATEGORY = CATT2.CATEGORY_CODE AND CATT2.CATEGORY_ID=29
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT3
			ON SALES_CM_CATEGORY = CATT3.CATEGORY_CODE AND CATT3.CATEGORY_ID=32
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT4
			ON CUSTOMER_PC_PRE_CATEGORY = CATT4.CATEGORY_CODE AND CATT4.CATEGORY_ID=10
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT5
			ON CUSTOMER_RO_CATEGORY = CATT5.CATEGORY_CODE AND CATT5.CATEGORY_ID=33
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT6
			ON CUSTOMER_BUSINESS_CATEGORY = CATT6.CATEGORY_CODE AND CATT6.CATEGORY_ID=41
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT7
			ON CUSTOMER_JOB_CATEGORY = CATT7.CATEGORY_CODE AND CATT7.CATEGORY_ID=42
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT8
			ON TAX_FRACT_CATEGORY = CATT8.CATEGORY_CODE AND CATT8.CATEGORY_ID=27
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT9
			ON PRICE_FRACT_CATEGORY = CATT9.CATEGORY_CODE AND CATT9.CATEGORY_ID=25
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT10
			ON PAYBACK_TYPE_CATEGORY = CATT10.CATEGORY_CODE AND CATT10.CATEGORY_ID=43
WHERE
	CUSTOMER_CODE = /*customerCode*/'S'
ORDER BY HIST_ID