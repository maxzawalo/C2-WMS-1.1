-- Обновляем ф-ии
drop  function if exists `GetDocDateByType` ;

CREATE  FUNCTION `GetDocDateByType`(reg_type int,reg_id int) RETURNS bigint(20)
BEGIN
DECLARE DocDate bigint(20);

CASE reg_type
	WHEN 1 THEN SET DocDate = (select doc.`docdate` from invoice doc where doc.id = reg_id);
	WHEN 2 THEN SET DocDate = (select doc.`docdate` from deliverynote doc where doc.id = reg_id);
    WHEN 3 THEN SET DocDate = (select doc.`docdate` from bill doc where doc.id = reg_id);
    WHEN 4 THEN SET DocDate = (select doc.`docdate` from `order` doc where doc.id = reg_id);    
	WHEN 5 THEN SET DocDate = (select doc.`docdate` from remaining_stock doc where doc.id = reg_id);
	WHEN 6 THEN SET DocDate = (select doc.`docdate` from cashvoucher doc where doc.id = reg_id);
	WHEN 7 THEN SET DocDate = (select doc.`docdate` from returnofgoods doc where doc.id = reg_id);
	WHEN 8 THEN SET DocDate = (select doc.`docdate` from writeoffproduct doc where doc.id = reg_id);
    WHEN 9 THEN SET DocDate = (select doc.`docdate` from paymentorder doc where doc.id = reg_id);
    WHEN 10 THEN SET DocDate = (select doc.`docdate` from returnfromcustomer doc where doc.id = reg_id);
    WHEN 11 THEN SET DocDate = (select doc.`docdate` from warrant4receipt doc where doc.id = reg_id);
	ELSE SET DocDate = '';
END CASE;
RETURN DocDate;
END;

-- Обновляем Контрагента в товарных регистрах
update registry_product rp set contractor_id = GetDocContractorByType(rp.reg_type, rp.reg_id);
update registry_product_fifo rp set contractor_id = GetDocContractorByType(rp.reg_type, rp.reg_id);