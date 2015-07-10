package net.ivanov.accountservice.server;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import net.ivanov.accountservice.api.AccountService;

public class AccountServiceImpl implements AccountService {
    private Map<Integer, AtomicLong> cache = new ConcurrentHashMap<>();
    private AccountDao accountDao;

    public AccountServiceImpl() throws Exception {
        accountDao = new AccountDao();
        loadCacheFromDb();
    }

    @Override
    public Long getAmount(Integer id) {
        AtomicLong balance = cache.get(id);

        if (balance == null) {
            return 0L;
        } else {
            return balance.get();
        }
    }

    @Override
    public void addAmount(Integer id, Long value) {
        AtomicLong balance = cache.get(id);

        if (balance == null) {
            balance = new AtomicLong(0);
            cache.put(id, balance);
        }
        balance.addAndGet(value);
        try {
            accountDao.save(id, balance.get());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCacheFromDb() throws SQLException {
        Collection<Account> accounts = accountDao.getAccounts();

        for (Account account : accounts) {
            long value = account.getBalance();

            AtomicLong balance = new AtomicLong(value);

            cache.put(account.getId(), balance);
        }
    }

}
