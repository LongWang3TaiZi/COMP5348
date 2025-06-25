import React, {useState, useRef, useEffect, useMemo} from 'react';
import apiClient from '../services/api';
import Table from 'react-bootstrap/Table';
import ListGroup from 'react-bootstrap/ListGroup';
import { Link } from 'react-router-dom';
function Delivery() {
    let [listData, setListData] = useState([])
    let isMounted = useRef(false)
    const listConfig = useRef([
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
        },
        {
            key: '',
            label: '-',
            type: 'operator'
        }
    ])
    const headerList = useMemo(() => {
        return listConfig.current.reduce((result, item) => {
            result.push(item.label)
            return result
        }, [])
    }, [listConfig])
    useEffect(() => {
        if (!isMounted.current) {
            getListData()
            isMounted.current = true
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isMounted])
    const getListData = async () => {
        try {
            const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
            let email = storedUser.email || ''
            if(!email){
                return false
            }
            // http://localhost:8080/comp5348/order/delivery/1@test.com
            const response = await apiClient.get(`/order/delivery/${email}`);
            let { deliveryInformations } = response.data
            if (deliveryInformations && Array.isArray(deliveryInformations)) {
                setListData(deliveryInformations)
            } else {
                setListData([])
            }
            console.log(response.data);
        } catch (error) {
            console.error('Error fetching delivery list:', error);
        }
    }

    return (
        <div className="delivery">
            <h2>Delivery Information</h2>
            <Table striped bordered hover responsive>
                <thead>
                <tr>
                    {headerList.map((item, index) => {
                        return (<th key={'header-'+item + index}>
                            {item}
                        </th>)
                    })}
                </tr>
                </thead>
                <tbody>
                {
                    listData.map((itemData, index) => {
                        return (<tr key={`tbody-row-${index}-${itemData.productName}-${itemData.id}`}>
                            {listConfig.current.map((colItem, colIndex) => {
                                if (colItem.type === 'operator') {
                                    return (<td key={`${colIndex}-${colItem.type}-${colItem.key}-${index}`}>
                                        <Link to={`/deliveryDetail/${itemData.id}`}>Detail</Link>
                                    </td>)
                                } else if (colItem.type === 'text') {
                                    return (<td key={`${colIndex}-${colItem.type}-${colItem.key}-${index} `}>
                                        {itemData[colItem.key]}
                                    </td>)
                                } else if (colItem.type === 'array') {
                                    return (<td key={`${colIndex}-${colItem.type}-${colItem.key}-${index}`}>
                                        <ListGroup>
                                            {(itemData[colItem.key] || []).map((arItem, arIndex) => {
                                                return <ListGroup.Item key={`row-${index}-col-${colIndex}-array-${arIndex}`}>{arItem}</ListGroup.Item>
                                            })}
                                        </ListGroup>
                                    </td>)
                                } else {
                                    return (<td></td>)
                                }

                            })}
                        </tr>)
                    })
                }
                </tbody>
            </Table>

            {/* TODO: add delivery information here */}
        </div>
    );
}

export default Delivery;