CREATE KEYSPACE starsettler WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '3'}  AND durable_writes = true;

CREATE TABLE starsettler.settlemessages (
    receiveddate text,
    ordernumber text,
    settledate text,
    cardtype text,
    transactiontype text,
    clientlineid text,
    transactionid text,
    addressline1 text,
    addressline2 text,
    addressline3 text,
    appeasementcode text,
    appeasementdate text,
    appeasementdescription text,
    appeasementreference text,
    authnum text,
    authoriztioncode text,
    avsresponsecode text,
    batchid text,
    cardreferene text,
    cardtoken text,
    city text,
    countrycode text,
    couponcode text,
    crc text,
    csvresponsecode text,
    descriptionfield text,
    email text,
    expirationdate text,
    firstname text,
    homephone text,
    identityuuid text,
    lastname text,
    lineid text,
    middlename text,
    orderdate text,
    paymentamount text,
    postalcode text,
    provincecode text,
    qualifiedplan text,
    quantity text,
    reasoncode text,
    requestplan text,
    responsedate text,
    responseplan text,
    responsereasoncode text,
    responsetype text,
    rrn text,
    sequenceid text,
    settleid text,
    settleplan text,
    settlestatus text,
    shipdate text,
    shipid text,
    shippingamount text,
    tokenbankname text,
    unit text,
    unitcost text,
    unitdiscount text,
    unittotal text,
    PRIMARY KEY (receiveddate, ordernumber, settledate, cardtype, transactiontype, clientlineid, transactionid)
) WITH CLUSTERING ORDER BY (ordernumber ASC, settledate ASC, cardtype ASC, transactiontype ASC, clientlineid ASC, transactionid ASC)
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


CREATE TABLE starsettler.processhistory (
    processdate text,
    batchid text,
    settlertype text,
    endtime text,
    starttime text,
    PRIMARY KEY (processdate, batchid, settlertype)
) WITH CLUSTERING ORDER BY (batchid ASC, settlertype ASC)
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

CREATE TABLE starsettler.batchidxref (
    receiveddate text,
    batchid text,
    sequenceid text,
    ordernumber text,
    processdate text,
    PRIMARY KEY (receiveddate, batchid, sequenceid, ordernumber)
) WITH CLUSTERING ORDER BY (batchid DESC, sequenceid ASC, ordernumber ASC)
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



CREATE TABLE starsettler.fileidref (
    processdate text,
    filesequenceid text,
    ordernumber text,
    receiveddate text,
    PRIMARY KEY (processdate, receiveddate, filesequenceid, ordernumber)
) WITH CLUSTERING ORDER BY (receiveddate DESC, filesequenceid ASC, ordernumber ASC)
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



alter table starsettler.settlemessages add time text;
alter table starsettler.settlemessages add trackdata2 text;
alter table starsettler.settlemessages add odometer text;
alter table starsettler.settlemessages add driverId text;
alter table starsettler.settlemessages add authreference text;
alter table starsettler.settlemessages add vehicleId text;
alter table starsettler.settlemessages add catflag text;
alter table starsettler.settlemessages add service text;

#alter table starsettler.settlemessages add productcode text;
alter table starsettler.settlemessages add filesequencenumber text;
alter table starsettler.settlemessages ADD nonfuelproductgroup list<text>;
alter table starsettler.settlemessages ADD fuelproductgroup list<text>;
alter table  settlemessages add pumpnumber text;

alter table starsettler.settlemessages add pumpNumber text;
alter table starsettler.settlemessages DROP nonfuelproductgroup ;
alter table starsettler.settlemessages DROP fuelproductgroup ;
alter table starsettler.settlemessages ADD productgroup list<text>;
alter table starsettler.settlemessages add date text;

CREATE TABLE starsettler.wexsettlemessages(
transactionfiledate text,
transactionfiletime text,
transactionfilesequence text,
tid text,
AppName text,
AppVersion text,
transcardCode text,
transnbr text,
transdate text,
transtime text,
cardtrack text,
pumpcat text,
pumpservice text,
pumpnbr text,
pumpamount text,
product list<text>,
odometer text,
amount text,
authref text,
driverid text,
vehicleid text,
orderDate text,
sequenceId text,
settleId text,
settlestatus text,
time text,
catflag text,
service text,
transactionId text,
ordernumber text, 
transactiontype text,
PRIMARY KEY (transactionfiledate, transactionfiletime, transactionfilesequence, transactionId, ordernumber , transactiontype));