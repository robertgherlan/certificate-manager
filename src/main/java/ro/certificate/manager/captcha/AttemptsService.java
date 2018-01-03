package ro.certificate.manager.captcha;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ro.certificate.manager.utils.ConfigurationUtils;

@Service
public class AttemptsService {

	@Autowired
	private ConfigurationUtils configurationUtils;

	private LoadingCache<String, Integer> attemptsCache;

	public AttemptsService() {
		super();
		attemptsCache = CacheBuilder.newBuilder()
				.expireAfterWrite(/* configurationUtils.getAttemptsInterval() */ 10, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(final String key) {
						return 0;
					}
				});
	}

	public void reCaptchaSucceeded(final String key) {
		attemptsCache.invalidate(key);
	}

	public void reCaptchaFailed(final String key) {
		int attempts = attemptsCache.getUnchecked(key);
		attempts++;
		attemptsCache.put(key, attempts);
	}

	public boolean isBlocked(final String key) {
		return attemptsCache.getUnchecked(key) >= configurationUtils.getMaxAttempts();
	}

	public LoadingCache<String, Integer> getAttemptsCache() {
		return attemptsCache;
	}

	public void setAttemptsCache(LoadingCache<String, Integer> attemptsCache) {
		this.attemptsCache = attemptsCache;
	}

}
