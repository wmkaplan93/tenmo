package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@Service
public class TransferSqlDAO implements TransferDAO {
	
	private JdbcTemplate jdbcTemplate;

	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	//post new transfer to table
	@Override
	public void logSend(long accountFrom, long accountTo, double amount) {
		String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
				+ "VALUES (2, 2, ?, ?, ?)";
		jdbcTemplate.update(sql, accountFrom, accountTo, amount);
	}
	
	//return list of transfers I'm involved in
	@Override
	public List<Transfer> myTransfers(long myAccountId) {
		Transfer thisTransfer = null;
		List<Transfer> myTransfers = new ArrayList<Transfer>();
		String sql = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, myAccountId, myAccountId);
		while (results.next()) {
			thisTransfer = mapRowToTransfer(results);
			myTransfers.add(thisTransfer);
		}
		return myTransfers;
	}
	
	@Override
    public List<Transfer> allTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfers";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }	
	@Override
	public Transfer transferDetails(int transferId) {
		for (Transfer transfer : this.allTransfers())
			if (transfer.getTransferId() == transferId) {
				return transfer;
			}
		System.out.println("Transfer not found.");
		return null;
	}

	
	private Transfer mapRowToTransfer(SqlRowSet results) {
		Transfer newTransfer = new Transfer();
		newTransfer.setAccountFrom(results.getInt("account_from"));
		newTransfer.setAccountTo(results.getInt("account_to"));
		newTransfer.setAmount(results.getDouble("amount"));
		newTransfer.setTransferId(results.getLong("transfer_id"));
		newTransfer.setTransferStatusId(results.getInt("transfer_status_id"));
		newTransfer.setTransferTypeId(results.getInt("transfer_type_id"));
		
		return newTransfer;
	}
	
	private long getNextTransferId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_transfer_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new transfer");
		}
	}
}
