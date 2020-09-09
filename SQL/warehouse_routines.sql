-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: warehouse
-- ------------------------------------------------------
-- Server version	5.7.18-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping routines for database 'warehouse'
--
/*!50003 DROP FUNCTION IF EXISTS `GetDocCodeByType` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetDocCodeByType`(reg_type int,reg_id int) RETURNS varchar(15) CHARSET utf8
BEGIN
DECLARE DocCode varchar(15);
 
CASE reg_type
    WHEN 1 THEN SET DocCode = (select doc.code from invoice doc where doc.id = reg_id);
	WHEN 2 THEN SET DocCode = (select doc.code from deliverynote doc where doc.id = reg_id);
    WHEN 3 THEN SET DocCode = (select doc.code from bill doc where doc.id = reg_id);
    WHEN 4 THEN SET DocCode = (select doc.code from `order` doc where doc.id = reg_id);    
	WHEN 5 THEN SET DocCode = (select doc.code from remaining_stock doc where doc.id = reg_id);
	WHEN 6 THEN SET DocCode = (select doc.code from cashvoucher doc where doc.id = reg_id);
	WHEN 7 THEN SET DocCode = (select doc.code from returnofgoods doc where doc.id = reg_id);
	WHEN 8 THEN SET DocCode = (select doc.code from writeoffproduct doc where doc.id = reg_id);
    WHEN 9 THEN SET DocCode = (select doc.code from paymentorder doc where doc.id = reg_id);
    WHEN 10 THEN SET DocCode = (select doc.code from returnfromcustomer doc where doc.id = reg_id);
    WHEN 11 THEN SET DocCode = (select doc.code from warrant4receipt doc where doc.id = reg_id);
    ELSE SET DocCode = '';
END CASE;
RETURN (DocCode);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetDocContractorByType` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetDocContractorByType`(reg_type int,reg_id int) RETURNS varchar(15) CHARSET utf8
BEGIN
DECLARE contractorId bigint(20);
 
CASE reg_type
    WHEN 1 THEN SET contractorId = (select doc.contractor_id from invoice doc where doc.id = reg_id);
	WHEN 2 THEN SET contractorId = (select doc.contractor_id from deliverynote doc where doc.id = reg_id);
    WHEN 3 THEN SET contractorId = (select doc.contractor_id from bill doc where doc.id = reg_id);
    WHEN 4 THEN SET contractorId = (select doc.contractor_id from `order` doc where doc.id = reg_id);    
	WHEN 5 THEN SET contractorId = (select doc.contractor_id from remaining_stock doc where doc.id = reg_id);
	WHEN 6 THEN SET contractorId = (select doc.contractor_id from cashvoucher doc where doc.id = reg_id);
	WHEN 7 THEN SET contractorId = (select doc.contractor_id from returnofgoods doc where doc.id = reg_id);
	WHEN 8 THEN SET contractorId = (select doc.contractor_id from writeoffproduct doc where doc.id = reg_id);
    WHEN 9 THEN SET contractorId = (select doc.contractor_id from paymentorder doc where doc.id = reg_id);
    WHEN 10 THEN SET contractorId = (select doc.contractor_id from returnfromcustomer doc where doc.id = reg_id);
    WHEN 11 THEN SET contractorId = (select doc.contractor_id from warrant4receipt doc where doc.id = reg_id);
    ELSE SET contractorId = 0;
END CASE;
RETURN (contractorId);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetDocDateByType` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetDocDateByType`(reg_type int,reg_id int) RETURNS bigint(20)
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `GetDocNameByType` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `GetDocNameByType`(reg_type int) RETURNS varchar(30) CHARSET utf8
BEGIN
DECLARE DocName varchar(30);
 
CASE reg_type
        WHEN 1 THEN SET DocName = 'Приходная';
		WHEN 2 THEN SET DocName = 'Расходная'; 
        WHEN 3 THEN SET DocName = 'Счет'; 
        WHEN 4 THEN SET DocName = 'Заказ'; 
		WHEN 5 THEN SET DocName = 'Оприходование';
        WHEN 6 THEN SET DocName = 'Чеки';
        WHEN 7 THEN SET DocName = 'Возврат поставщику';
		WHEN 8 THEN SET DocName = 'Списание товаров';
        WHEN 9 THEN SET DocName = 'Платежное поручение';
        WHEN 10 THEN SET DocName = 'Возврат от покупателя';
        WHEN 11 THEN SET DocName = 'Доверенность';
        ELSE SET DocName = '';
END CASE;
RETURN (DocName);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `now_microseconds` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `now_microseconds`() RETURNS bigint(20)
BEGIN
	RETURN (select ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000));
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `ShowDate` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `ShowDate`(`date` bigint(20)) RETURNS varchar(8) CHARSET utf8
BEGIN

RETURN from_unixtime(`date`/ 1000, '%d.%m.%y');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `CostPriceView` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `CostPriceView`()
BEGIN
SELECT
		`doc_type`,
		ShowDate(`docdate`) `doc_date`,
		doc_code,
		product,
		count,
		cost_price,
        price
FROM
(
	(SELECT
		GetDocNameByType(2) `doc_type`,
		doc.`docdate`,
		doc.code doc_code,
		p.name product,
		tp.count,
		tp.price_discount_off cost_price,
        tp.price
	FROM
	deliverynote_tp_product tp
	join product p on tp.product_id = p.id
	join deliverynote doc on doc.id = tp.doc_id
    where not tp.deleted and tp.sync_flag = 0 
	)
	union all
	(SELECT 
		GetDocNameByType(6) `doc_type`,
		doc.`docdate`,
		doc.code doc_code,
		p.name product,
		tp.count,
		tp.price_discount_off cost_price,
        tp.price
	FROM
	cashvoucher_tp_product tp
	join product p on tp.product_id = p.id
	join cashvoucher doc on doc.id = tp.doc_id
    where not tp.deleted and tp.sync_flag = 0 
	)
)t
 order by docdate,doc_type , doc_code;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_contract` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_contract`(IN start_time bigint(20))
BEGIN
SELECT 
contract.*, 
(CASE contract.contract_type_id 
	WHEN 1 THEN 'С поставщиком'
    WHEN 2 THEN 'С покупателем'
    WHEN 3 THEN 'Прочее'
    ELSE ''
END) contract_type,
`owner`.code owner_code, 
currency.code currency_code  
FROM contract 
left join contractor `owner` on contract.owner_id = `owner`.id
left join currency on contract.doc_currency_id = currency.id
where contract.sync_flag = 0 and contract.changed >= start_time;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_contractor` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_contractor`(IN start_time bigint(20))
BEGIN
SELECT 
	contractor.*,
    parent.code parent_code,
	ifnull(legal_address.name,'') legal_address,
    ifnull(post_address.name,'') post_address,
    ifnull(real_address.name,'') real_address,
    ifnull(phones.name,'') phones,
    ifnull(fax.name,'') fax
FROM contractor 
	left join contractor parent on contractor.parent_id  = parent.id
	left join contactinfo legal_address on legal_address.owner_id = contractor.id and legal_address.contact_type ='Юридический адрес'
	left join contactinfo post_address on post_address.owner_id = contractor.id and post_address.contact_type ='Почтовый адрес'
    left join contactinfo real_address on real_address.owner_id = contractor.id and real_address.contact_type ='Фактический адрес'
    left join contactinfo phones on phones.owner_id = contractor.id and phones.contact_type ='Телефоны'
    left join contactinfo fax on fax.owner_id = contractor.id and fax.contact_type ='Факс'
where contractor.sync_flag = 0 and contractor.changed >= start_time;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_deliverynote` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_deliverynote`(IN start_time bigint(20), IN end_time bigint(20))
BEGIN
SELECT 
	deliverynote.*, 
	contractor.code contractor_code, 
	contract.code contract_code, 
    currency.code currency_code,
    shipment_permited.code shipment_permited_code,
    shipper_hand_in.code shipper_hand_in_code,
    shipment_produced.code shipment_produced_code,
    `client`.code client_code,
    store.code store_code
FROM deliverynote 
	left join contractor on deliverynote.contractor_id = contractor.id
	left join contract on deliverynote.doc_contract_id = contract.id 
	left join currency on deliverynote.doc_currency_id = currency.id 
    left join coworker shipment_permited on deliverynote.shipment_permited_id = shipment_permited.id 
    left join coworker shipper_hand_in on deliverynote.shipper_hand_in_id = shipper_hand_in.id 
    left join coworker shipment_produced on deliverynote.shipment_produced_id = shipment_produced.id 
    left join contractor `client` on deliverynote.client_id = `client`.id
    left join store on deliverynote.store_id = store.id

where deliverynote.sync_flag = 0 
and (deliverynote.created >= start_time or deliverynote.changed >= start_time or deliverynote.DocDate >= start_time)
and (deliverynote.created <= end_time or deliverynote.changed <= end_time or deliverynote.DocDate <= end_time);

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_lots_by_product_id` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_lots_by_product_id`(IN product_id int(11))
BEGIN
SET sql_mode = '';
select 
IFNULL(reg.lotOfProduct_id, 0) as `id`, 

 IFNULL( round(lot.price,2), 0) as `price`, 
 IFNULL( round(lot.price*1.2,2), 0) as `total`, 
 (CASE reg.reg_type 
	WHEN 1 THEN (select 'Приходная')
	WHEN 2 THEN (select 'Расходная')
    WHEN 5 THEN (select 'Остатки')
	WHEN 6 THEN (select 'Чеки')
    WHEN 7 THEN (select 'Возврат поставщику')
END) `doc_type`,
(CASE reg.reg_type 
	WHEN 1 THEN (select from_unixtime(`docdate`/ 1000, '%d.%m.%y') from invoice doc where doc.id = reg.reg_id)
    WHEN 2 THEN (select from_unixtime(`docdate`/ 1000, '%d.%m.%y') from deliverynote doc where doc.id = reg.reg_id)
	WHEN 5 THEN (select from_unixtime(`docdate`/ 1000, '%d.%m.%y') from remaining_stock doc where doc.id = reg.reg_id)
	WHEN 6 THEN (select from_unixtime(`docdate`/ 1000, '%d.%m.%y') from cashvoucher doc where doc.id = reg.reg_id)
    WHEN 7 THEN (select from_unixtime(`docdate`/ 1000, '%d.%m.%y') from returnofgoods doc where doc.id = reg.reg_id)
END) `doc_date`,

sum(IFNULL(reg.count, 0)) as `count`
from `registry_product` reg
	join lotofproduct lot ON reg.lotOfProduct_id = lot.id 
WHERE reg.deleted=false AND lot.deleted=false 
and reg.`product_id` = product_id
group by id;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_price` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_price`(IN start_time bigint(20), IN end_time bigint(20))
BEGIN
SELECT 
	price.*,
    product.code product_code,
    invoice.code invoice_code,
    invoice.DocDate invoice_date,
    invoice.uuid invoice_uuid
FROM price
	join product on product.id = price.product_id
	
    join invoice on invoice.id = price.invoice_id
where price.sync_flag = 0 
and (price.created >= start_time or price.changed >= start_time)
and (price.created <= end_time or price.changed <= end_time);

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_price_by_code` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_price_by_code`(IN code varchar(15))
BEGIN
SELECT 
	product.name, 
    price.price, 
    price.total, 
    units.name units ,
    product.id product_id
FROM price 
	join product on price.product_id = product.id
	join units on product.units_id = units.id
where 
price.code = code;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_price_by_product_code` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_price_by_product_code`(IN code varchar(15))
BEGIN
SELECT 
	product.name, 
    0 price, 
    0 total, 
    units.name units ,
    product.id product_id
FROM product
	join units on product.units_id = units.id
where 
product.code = code;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_product` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_product`(IN start_time bigint(20))
BEGIN
SELECT product.*, units.code units_code, parent.code parent_code FROM product 
left join units on units.id = product.units_id
left join product parent on product.parent_id  = parent.id
where product.sync_flag = 0 and product.changed >= start_time;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `insert_invertory_check` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_invertory_check`(in product_id int(20), in count double, in real_count double)
BEGIN

DECLARE now bigint(20);
SET now = now_microseconds();
insert into registry_inventory 
(product_id, count, real_count,
sync_flag,
reg_date,
created,
`changed`,
deleted) 
values 
(product_id,count, real_count,
0,
now,
now,
now,
0);

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `insert_store_record` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_store_record`(in user_id int(11),in product_id int(11), in contractor_id int(11), in count double, in price double, in who_recieve varchar(50))
BEGIN
DECLARE deleted tinyint(1);
DECLARE uuid varchar(48);
DECLARE created_by_id,changed_by_id, locked_by_id, sync_flag, link_id int(11);

DECLARE changed, created,entry_time bigint(20);
DECLARE code varchar(15); 
DECLARE comment varchar(100);
DECLARE device varchar(10);
DECLARE hash, sign varchar(64);

SET code  = '';
SET deleted = 0;
SET sync_flag = 0;
SET locked_by_id = 0;

SET created_by_id = user_id;
SET changed_by_id = user_id;
SET uuid = '00000000-0000-0000-0000-000000000000';

SET created = now_microseconds();
SET changed = created;
SET entry_time = changed;

SET device = 'mobile';
SET comment = '';
SET link_id = 0;
SET hash ='';
SET sign ='';


INSERT INTO `storedaybook`
(`entry_time`,
`contractor_id`,
`product_id`,
`price`,
`count`,
`link_id`,
`data`,
`hash`,
`comment`,
`who_recieve`,
`code`,
`created`,
`changed`,
`deleted`,
`sync_flag`,
`uuid`,
`created_by_id`,
`changed_by_id`,
`device`)
VALUES
(entry_time,
contractor_id,
product_id,
price,
count,
link_id,
data,
hash,
comment,
who_recieve,
code,
created,
changed,
deleted,
sync_flag,
uuid,
created_by_id,
changed_by_id,
device);

select 'ok';


END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-09-09 14:02:18
