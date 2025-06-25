import React, {useState, useEffect, useRef} from 'react'
import apiClient from '../services/api'
import {useParams, Link} from 'react-router-dom';
import ListGroup from 'react-bootstrap/ListGroup';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
export default function DeliveryDetail() {
  let [detailData, setDetailData] = useState({})
  const {id} = useParams();
  let detailConfig = useRef([
    {
      key: 'productName',
      label: 'productName',
      type: 'text'
    },
    {
      key: 'quantity',
      label: 'quantity',
      type: 'text'
    },
    {
      key: 'email',
      label: 'email',
      type: 'text'
    },
    {
      key: 'creationTime',
      label: 'creationTime',
      type: 'text'
    },
    {
      key: 'updateTime',
      label: 'updateTime',
      type: 'text'
    },
    {
      key: 'fromAddress',
      label: 'fromAddress',
      type: 'array'
    },
    {
      key: 'toAddress',
      label: 'toAddress',
      type: 'text'
    },
    {
      key: 'deliveryStatus',
      label: 'deliveryStatus',
      type: 'text'
    }
  ])

  useEffect(() => {
    console.log('deliveryDetail---')
    if (id && !Object.keys(detailData).length) {
      getDetailData()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])
  const getDetailData = (async () => {
    try {
      // http://localhost:8080/comp5348/order/delivery/detail/802
      const response = await apiClient.get(`/order/delivery/detail/${id}`);
      if (response.data && response.data.deliveryInformation) {
        setDetailData(response.data.deliveryInformation)
      } else {
        setDetailData({})
      }
      console.log('fetch delivery data result=', response.data);
    } catch (error) {
      console.error('Error fetching delivery detail:', error);
    }
  })
  return (<div>
    <Link to={`/delivery`} replace>Back</Link>

    <Container fluid>
      {detailConfig.current.map((colItem, colIndex) => {
        return <Row style={{padding: '20px'}} key={colIndex}>
          <Col md={2}>{colItem.label}</Col>
          {colItem.type === 'text' && <Col>{detailData[colItem.key]}</Col>}
          {colItem.type === 'array' && <Col>
            <ListGroup>
              {(detailData[colItem.key] || []).map((arItem, arIndex) => {
                return <ListGroup.Item key={`${colIndex}-array-${arIndex}`}>{arItem}</ListGroup.Item>
              })}
            </ListGroup>
          </Col>}

        </Row>
      })
      }
    </Container>

  </div>)
}