import React, { useState } from 'react';
import axios from 'axios';
import {Box, Button, InputLabel, TextField, Typography} from '@mui/material';
import config from "../config";

const ImageGallery = () => {
    const [filterWord, setFilterWord] = useState('');
    const [images, setImages] = useState([]);
    const apiUrl = config.API_URL;

    const handleFilterChange = (e) => {
        setFilterWord(e.target.value);
    };

    const fetchImages = async () => {
        try {
            const response = await axios.get(`${apiUrl}/api/v1/get-by-filter`, {
                params: { filterWord }
            });

            setImages(response.data);
        } catch (error) {
            console.error('Error fetching images:', error);
        }
    };

    const handleSearchClick = () => {
        if (filterWord) {
            fetchImages();
        } else {
            alert('Please enter a filter word');
        }
    };

    return (
        <div>
            <Box
                sx={{
                    display: 'flex',
                    gap: 2,
                    alignItems: 'center',
                    marginBottom: 5,
                    marginLeft: 20,
                }}
            >
                <InputLabel
                    shrink={false}
                    htmlFor={"filterWord"}
                >
                    <Typography>Please find me images that contain the following items: </Typography>
                </InputLabel>
                <TextField
                    label="Filter Word"
                    value={filterWord}
                    onChange={handleFilterChange}
                    variant="outlined"
                />
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSearchClick}
                    sx={{ height: '100%' }}
                >
                    Search
                </Button>
            </Box>

            <div className="image-gallery">
                {images.length > 0 ? (
                    <Box
                        sx={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(3, 1fr)',
                            gap: 2,
                            marginTop: 2,
                            marginLeft: 20,
                            maxWidth: 1000,
                        }}
                    >
                        {images.map((image, index) => (
                            <div key={index} className="image-item">
                                <img
                                    src={image}
                                    alt={`Filtered object ${index}`}
                                    style={{
                                        width: '100%',
                                        height: 'auto',
                                        borderRadius: '8px',
                                    }}
                                />
                                <div className="fade-effect"></div>
                            </div>
                        ))}
                    </Box>
                ) : (
                    <Typography sx={{marginLeft: 20}}>No images found</Typography>
                )}
            </div>

            <style jsx>{`
                .image-item {
                    position: relative;
                    overflow: hidden;
                    border-radius: 8px;
                }

                .fade-effect {
                    position: absolute;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    height: 20px;
                    background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.5));
                }
            `}</style>
        </div>
    );
};

export default ImageGallery;
