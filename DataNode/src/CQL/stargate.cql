
CREATE KEYSPACE stargate WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '3'}  AND durable_writes = true;

CREATE TABLE stargate.facmapper (
    uuid text PRIMARY KEY,
    devicetype text,
    facility text,
    strategy text,
    tokenbankname text
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE stargate.transactions (
    authhour text,
    ordernumber text,
    account text,
    accounttypetype text,
    actioncode text,
    amount bigint,
    amountsign text,
    amtpreauthorized bigint,
    authnumber text,
    authoriztioncode text,
    avsresponsecode text,
    balanceamount bigint,
    billingaddress1 text,
    billingaddress2 text,
    billingcountrycode text,
    billingphone text,
    billpaymentindicator text,
    billingzipcode text,
    cardholdername text,
    cardPresence text,
    cardreferenceid text,
    cardsequencenumber text,
    comment text,
    contact text,
    csvresponsecode text,
    customerid text,
    currencycode text,
    datanumber text,
    deptnumber text,
    descriptionfield text,
    divisionnumber text,
    downpayment text,
    driverid text,
    email text,
    encryptalgo text,
    encryptedpayload text,
    encryptmgmt text,
    encrypttrack text,
    essoloadamount bigint,
    expiration text,
    facility text,
    facility10 text,
    facility7 text,
    fee text,
    fuelprice bigint,
    fuelprodcode text,
    identityuuid text,
    inputtype text,
    inputCode text,
    jobvaluenumber text,
    ksn text,
    licencenumber text,
    localdatetime text,
    media text,
    merchantorg text,
    milstarnumber text,
    modifiedacctvalue text,
    odometer text,
    origaccttype text,
    origauthcode text,
    originalorder text,
    originalrequesttype text,
    origtransid text,
    pan text,
    partialamount bigint,
    paymenttype text,
    pinblock text,
    plannumber text,
    proddetailcount text,
    productcode text,
    pumpnmbr text,
    pumpprice bigint,
    qtypumped bigint,
    rationamt text,
    reasoncode text,
    requestauthdatetime text,
    requesttype text,
    requestxmldatetime text,
    responseauthdatetime text,
    responsedate text,
    responsetype text,
    responsexmldatetime text,
    restrictcode text,
    reversal text,
    rrn text,
    sequenceNumber text, 
    servicecode text,
    settleamt bigint,
    settleindicator text,
    settlerq text,
    settlers text,
    shippingaddress text,
    shippingcountrycode text,
    shippingphone text,
    shippingzipcode text,
    skunumber text,
    stan text,
    telephonetype text,
    termid text,
    tokenid text,
    traceid text,
    track1 text,
    track2 text,
    transactionid text,
    transactiontype text,
    unitmeas text,
    unitofmeas text,
    upc text,
    userid text,
    vehicleid text,
    voidflag text,
    zipcode text,
    PRIMARY KEY (identityuuid, rrn, requesttype)
) WITH CLUSTERING ORDER BY (rrn ASC)
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';


CREATE TABLE stargate.gcbank (
    cardNumber text,
    pin text,
    balanceAmount text,
    PRIMARY KEY (cardNumber, pin)
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

truncate stargate.facmapper;

insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values('c47e4366-fe72-473b-b114-523e8de5641f','i','3858000000','Ecomm','Ecomm001');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values('This is Jenkins Deploy Test','null','1234567888','Ecomm','EcommTest');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values('27cd5bc9-3ade-4cfc-a72e-4d413f286023','RPOS','3740152100','Deca','Deca001');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('3c605626-de69-40ea-bde1-ff7de1a31ba7','RPOS','3740152100','Deca','Deca002');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('3ca800ee-0809-11e7-93ae-92361f002671','RPOS','3740152100','Deca','Deca003');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('7c596d3d-e7ce-4f6a-bdbc-36182fae76aa','RPOS','3740152100','Deca','Deca004');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('12244f25-6f1b-4eac-bfbc-2a179982d83e','RPOS','3740152100','Deca','Deca005');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('0ee1c509-2c70-4bcd-b261-f94f1fe6c43b','RPOS','3740152100','Deca','Deca006');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('515dc3d5-839f-4c5e-bd8b-1fe3bce677e5','RPOS','3740152100','Deca','Deca007');
insert into stargate.facmapper(uuid,devicetype,facility,strategy,tokenbankname) values ('ecca7eb6-1314-4cb4-b156-eaf6929aebd5','i','3858000000','Ecomm','Ecomm002');

insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491571000000111','1111','10000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491571000000112','1112','20000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491571000000113','1113','30000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491571000000114','1114','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002421','3685','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002439','7020','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002447','8356','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002454','0934','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002462','4612','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002470','5546','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002488','1177','40000');
insert into stargate.gcbank(cardNumber,pin,balanceAmount) values('6006491572010002496','0118','40000');


