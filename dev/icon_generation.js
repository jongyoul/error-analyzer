const https = require('https');
const fs = require('fs');

const OPENAI_API_KEY = process.env.API_TOKEN;

const prompt = "Icon for indicating something analyze Error";

const requestData = JSON.stringify({
  prompt: prompt,
  n: 2, // Number of images to generate
  size: "512x512", // Image size
});

const options = {
  hostname: 'api.openai.com',
  path: '/v1/images/generations',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${OPENAI_API_KEY}`,
  },
};

const req = https.request(options, (res) => {
  let data = '';

  res.on('data', (chunk) => {
    data += chunk;
  });

  res.on('end', () => {
    const response = JSON.parse(data);

    // Save the API response to a local file
    fs.writeFileSync('openai_response.json', JSON.stringify(response, null, 2));

    // Download images
    if (response.data && response.data.length > 0) {
      for (let i = 0; i < response.data.length; i++) {
        const imageUrl = response.data[i].url;
        downloadImage(imageUrl, `image_${i}.png`);
      }
    } else {
      console.log('No images found.');
    }
  });
});

function downloadImage(url, filename) {
  https.get(url, (response) => {
    const fileStream = fs.createWriteStream(filename);

    response.pipe(fileStream);

    fileStream.on('finish', () => {
      fileStream.close();
      console.log(`Image '${filename}' downloaded successfully`);
    });
  });
}

req.on('error', (error) => {
  console.error(error);
});

req.write(requestData);
req.end();
