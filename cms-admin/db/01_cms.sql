CREATE OR REPLACE FUNCTION getPartitionTableName (
	IN masterTable CHARACTER VARYING, 
	IN uniqueColumn CHARACTER VARYING, 
	IN partitionIdColumn CHARACTER VARYING, 
	IN partitionIdValue INTEGER,
	IN indexColumn1 CHARACTER VARYING DEFAULT NULL,
	IN indexColumn2 CHARACTER VARYING DEFAULT NULL,
	IN indexColumn3 CHARACTER VARYING DEFAULT NULL
) 
RETURNS CHARACTER VARYING AS
$$
DECLARE
	tableName CHARACTER VARYING;
	tableIndex CHARACTER VARYING;
BEGIN
	tableName := masterTable || '_c' || lpad(CAST(partitionIdValue AS VARCHAR(4)), 4, '0');
	tableIndex := replace(tableName, '.', '_');

	BEGIN
		IF uniqueColumn IS NOT NULL THEN
			EXECUTE 'CREATE TABLE IF NOT EXISTS ' || tableName || ' (UNIQUE(' || uniqueColumn || '), ' ||
				'CHECK (' || partitionIdColumn || ' = ' || partitionIdValue || ')) INHERITS (' || masterTable || ')';
		ELSE
			EXECUTE 'CREATE TABLE IF NOT EXISTS ' || tableName || ' (' ||
				'CHECK (' || partitionIdColumn || ' = ' || partitionIdValue || ')) INHERITS (' || masterTable || ')';
		END IF;
	END;

	BEGIN
		EXECUTE 'CREATE INDEX IF NOT EXISTS ' || tableIndex || '_' || partitionIdColumn || '_idx ON ' || 
			tableName || ' (' || partitionIdColumn || ')';

		IF indexColumn1 IS NOT NULL THEN
			EXECUTE 'CREATE INDEX IF NOT EXISTS ' || tableIndex || '_' || indexColumn1 || '_idx ON ' || 
				tableName || ' (' || indexColumn1 || ')';
		END IF;
		
		IF indexColumn2 IS NOT NULL THEN
			EXECUTE 'CREATE INDEX IF NOT EXISTS ' || tableIndex || '_' || indexColumn2 || '_idx ON ' || 
				tableName || ' (' || indexColumn2 || ')';
		END IF;
		
		IF indexColumn3 IS NOT NULL THEN
			EXECUTE 'CREATE INDEX IF NOT EXISTS ' || tableIndex || '_' || indexColumn3 || '_idx ON ' || 
				tableName || ' (' || indexColumn3 || ')';
		END IF;
		
	END;

	RETURN tableName;
END;
$$ LANGUAGE plpgsql;