INSERT INTO DISCOUNT_TRN_/*$domainId*/
(
	DISCOUNT_DATA_ID,
	DISCOUNT_ID,
	LINE_NO,
	DATA_FROM,
	DATA_TO,
	DISCOUNT_RATE,
	CRE_FUNC,
	CRE_DATETM,
	CRE_USER,
	UPD_FUNC,
	UPD_DATETM,
	UPD_USER
)
VALUES
(
	/*discountDataId*/,
	/*discountId*/,
	/*lineNo*/,
	/*dataFrom*/,
	/*dataTo*/,
	/*discountRate*/,
	/*creFunc*/,
	now(),
	/*creUser*/,
	/*updFunc*/,
	now(),
	/*updUser*/
)
