# Base image
FROM python:3.10 

# Install New Relic
RUN pip install --no-cache-dir newrelic

# Set environment variables
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    NEW_RELIC_CONFIG_FILE=/newrelic.ini

# Set the working directory
WORKDIR /myapp

# Copy the New Relic configuration file to the container
COPY newrelic.ini /newrelic.ini

# Copy dependencies 
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r ./requirements.txt

# Copy the rest of the project files
COPY . .

# Expose the desired port
EXPOSE 8060

# Use New Relic's admin tool to wrap the Django runserver command
ENTRYPOINT ["newrelic-admin", "run-program"]

# Run the Django server on localhost and port 8060
CMD ["python", "manage.py", "runserver", "0.0.0.0:8060"]

