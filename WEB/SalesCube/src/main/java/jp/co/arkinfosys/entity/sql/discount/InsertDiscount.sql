INSERT INTO DISCOUNT_MST_/*$domainId*/
(
	DISCOUNT_ID,
	DISCOUNT_NAME,
	REMARKS,
	USE_FLAG,
	CRE_FUNC,
	CRE_DATETM,
	CRE_USER,
	UPD_FUNC,
	UPD_DATETM,
	UPD_USER
)
VALUES
(
	/*discountId*/,
	/*discountName*/,
	/*remarks*/,
	/*useFlag*/,
	/*creFunc*/,
	now(),
	/*creUser*/,
	/*updFunc*/,
	now(),
	/*updUser*/
)