SELECT
    count(*)
FROM
	SUPPLIER_LINE_TRN_/*$domainId*/ SL
	INNER JOIN PAYMENT_LINE_TRN_/*$domainId*/ PL
		ON SL.SUPPLIER_LINE_ID = PL.SUPPLIER_LINE_ID
WHERE
	SL.SUPPLIER_SLIP_ID = /*supplierSlipId*/16374
FOR UPDATE