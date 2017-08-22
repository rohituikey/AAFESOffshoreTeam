
CREATE TABLE starsettler.wexsettlemessages(
filesequenceId text, 
tid text,
AppName text,
AppVersion text,
cardtrack text,
pumpcat text,
pumpservice text,
product list<text>,
odometer text,
amount text,
authref text,
driverid text,
vehicleid text,
recieDate text,
settlestatus text,
catflag text,
service text,
transactionId text,
ordernumber text, 
transactiontype text,
transactioncode text,
settelmentdate text,
settelmenttime text
PRIMARY KEY (tid, recieDate ,settlestatus, traansactionType,ordernumber)
)WITH CLUSTERING ORDER BY (tid DESC, ordernumber ASC)
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
