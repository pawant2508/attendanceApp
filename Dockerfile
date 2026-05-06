# Use the official PHP image with Apache
FROM php:8.2-apache

# Install PostgreSQL client and PDO extension
RUN apt-get update && apt-get install -y \
    libpq-dev \
    && docker-php-ext-install pdo pdo_pgsql

# Copy application files from server directory
COPY server/ /var/www/html/

# Set permissions
RUN chown -R www-data:www-data /var/www/html/

# Expose port 80
EXPOSE 80

# Start Apache
CMD ["apache2-foreground"]